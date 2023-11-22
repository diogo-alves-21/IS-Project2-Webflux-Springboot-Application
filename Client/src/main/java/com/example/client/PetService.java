package com.example.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.retry.Retry;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

import java.util.stream.Collectors;

@Service
public class PetService {

    private final WebClient webClient;


    @Autowired
    public PetService(WebClient.Builder webClientBuilder){
        webClient = webClientBuilder.build();
    }

    public void getTotalOfPets() throws FileNotFoundException {
        PrintStream fileOutputStream = new PrintStream(new FileOutputStream("./src/main/java/com/example/client/files/exercise2"));
        webClient.get()
                .uri("/pets")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(JsonNode::size)
                .doOnNext(output -> {
                    String outputText = "Total number of pets: " + output;
                    fileOutputStream.println(outputText);
                })
                .doFinally(signal -> fileOutputStream.close())
                .retryWhen(Retry
                        .max(3)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            throw new RuntimeException("Max retries exceeded");
                        }))
                .subscribe(totalCount -> System.out.println("\nTotal number of pets: " + totalCount+"\n"));
    }

    public void getTotalOfDogs() throws FileNotFoundException {
        PrintStream fileOutputStream = new PrintStream(new FileOutputStream("./src/main/java/com/example/client/files/exercise3"));
        webClient.get()
                .uri("/pets")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .filter(i-> i.get("specie").asText().equals("dog"))
                .count()
                .doOnNext(output->{
                    String outputText = "Total number of dogs: " + output;
                    fileOutputStream.println(outputText);
                })
                .doFinally(signal -> fileOutputStream.close())
                .retryWhen(Retry
                        .max(3)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            throw new RuntimeException("Max retries exceeded");
                        }))
                .subscribe(totalCount -> System.out.println("Total number of dogs: " + totalCount+"\n"));
    }

    public void getTotalPetsWithMoreThan10Kg() throws FileNotFoundException {
        PrintStream fileOutputStream = new PrintStream(new FileOutputStream("./src/main/java/com/example/client/files/exercise4"));
        webClient.get()
                .uri("/pets")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .doOnSubscribe(text-> System.out.println("List of the animals weighing more 10 kg:"))
                .filter(i -> i.get("weight").floatValue() > 10)
                .sort(Comparator.comparing(i -> i.get("weight").floatValue()))
                .doOnNext(output->{
                    String outputText = output.get("name").asText() + " - " + output.get("weight").floatValue() + " kg";
                    fileOutputStream.println(outputText);
                })
                .doFinally(signal -> fileOutputStream.close())
                .retryWhen(Retry
                    .max(3)
                    .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                    throw new RuntimeException("Max retries exceeded");
                }))
                .subscribe(animal -> {
                    System.out.println(animal.get("name").asText() + " - " + animal.get("weight").floatValue() + " kg");
                });
    }

    public void getAverageAndDeviationOfWeights() throws FileNotFoundException {
        PrintStream fileOutputStream = new PrintStream(new FileOutputStream("./src/main/java/com/example/client/files/exercise5"));
        webClient.get()
                .uri("/pets")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .map(animal-> animal.get("weight").floatValue())
                .reduce(new float[]{0, 0, 0}, (acc, weight) -> {
                    acc[0] += weight;
                    acc[1] += weight * weight;
                    acc[2]++;
                    return acc;
                })
                .map(acc -> {
                    float sum = acc[0];
                    float sumOfSquares = acc[1];
                    int count = (int) acc[2];
                    float average = sum / count;
                    double standardDeviation = Math.sqrt((sumOfSquares / count) - (average * average));
                    return "\nAverage of animal weights: " + average + " kg\nStandard Deviation of animal weights: " + (float)standardDeviation + " kg\n";
                })
                .doOnNext(fileOutputStream::println)
                .doFinally(signal -> fileOutputStream.close())
                .retryWhen(Retry
                .max(3)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                    throw new RuntimeException("Max retries exceeded");
                }))
                .subscribe(System.out::println);
    }

    public void getNameOfTheEldestPet() throws FileNotFoundException {
        PrintStream fileOutputStream = new PrintStream(new FileOutputStream("./src/main/java/com/example/client/files/exercise6"));
        webClient.get()
                .uri("/pets")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .map(animal -> {
                    String date = animal.get("birth_date").asText();
                    String age="";
                    int index = date.indexOf("-");
                    if (index != -1) {
                        age = date.substring(0, index);
                    }
                    //return new Object[]{Integer.parseInt(age), animal.get("name").asText()};
                    return Map.entry(Integer.parseInt(age), animal.get("name").asText());
                })
                .reduce((oldest, current) -> {
                    if (current.getKey() > oldest.getKey()) {
                        return oldest;
                    } else {
                        return current;
                    }
                })
                .doOnNext(output->{
                    String outputText = "Name of the eldest pet: "+output.getValue();
                    fileOutputStream.println(outputText);
                })
                .doFinally(signal -> fileOutputStream.close())
                .retryWhen(Retry
                        .max(3)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            throw new RuntimeException("Max retries exceeded");
                        }))
                .subscribe(oldestPet-> {
                    System.out.println("Name of the eldest pet: "+oldestPet.getValue()+"\n");
                });
    }

    public void getAveragePetsPerOwner() throws FileNotFoundException {
        PrintStream fileOutputStream = new PrintStream(new FileOutputStream("./src/main/java/com/example/client/files/exercise7"));
        webClient.get()
                .uri("/pets")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .groupBy(pet -> pet.get("ownerid").asInt())
                .flatMap(Flux::count)
                .filter(count -> count > 1)
                .collect(Collectors.toList())
                .map(ownerPetCounts -> {
                    if (ownerPetCounts.isEmpty()) {
                        return 0.0;
                    } else {
                        long totalOwners = ownerPetCounts.size();
                        long totalPetCount = 0;
                        for (long count : ownerPetCounts) {
                            totalPetCount += count;
                        }
                        return (float) totalPetCount / totalOwners;
                    }
                })
                .doOnNext(output->{
                    String outputText = "Average number of pets per owner: "+output;
                    fileOutputStream.println(outputText);
                })
                .doFinally(signal -> fileOutputStream.close())
                .retryWhen(Retry
                        .max(3)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            throw new RuntimeException("Max retries exceeded");
                        }))
                .subscribe(average -> System.out.println("Average number of pets per owner: "+average+"\n"));
    }


}
