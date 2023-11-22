package com.example.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.io.*;
import java.time.Duration;
import java.util.Map;

@Service
public class OwnerService {

    private final WebClient webClient;

    @Autowired
    public OwnerService(WebClient.Builder webClientBuilder){
        webClient = webClientBuilder.build();
    }



    public void getAllOwnersNamesAndTelephones() throws FileNotFoundException {
        PrintStream fileOutputStream = new PrintStream(new FileOutputStream("./src/main/java/com/example/client/files/exercise1"));
        webClient.get()
                .uri("/owners")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .map(jsonNode ->"Name: " + jsonNode.get("name").asText() +" - Telephone: "+jsonNode.get("number"))
                .doOnNext(fileOutputStream::println)
                .doFinally(signal -> fileOutputStream.close())
                .retryWhen(Retry
                        .max(3)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            throw new RuntimeException("Max retries exceeded");
                        }))
                .subscribe(System.out::println);
    }

    public Flux<Map.Entry<String, Long>> getNameOfOwnerAndNumberOfPets() throws FileNotFoundException {
        PrintStream fileOutputStream = new PrintStream(new FileOutputStream("./src/main/java/com/example/client/files/exercise8"));
        return webClient.get()
                .uri("/owners")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .flatMap(owner -> {
                    String ownerId = owner.get("id").asText();
                    String ownerName = owner.get("name").asText();
                    return webClient.get()
                            .uri("/pets")
                            .retrieve()
                            .bodyToFlux(JsonNode.class)
                            .filter(pet -> ownerId.equals(pet.get("ownerid").asText()))
                            .count()
                            .map(count -> Map.entry(ownerName, count));
                })
                .sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .doOnNext(output->{
                    String outputText = "Name: "+output.getKey() + "\tNumber of pets: "+output.getValue();
                    fileOutputStream.println(outputText);
                })
                .doFinally(signal -> fileOutputStream.close())
                .retryWhen(Retry
                        .max(3)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            throw new RuntimeException("Max retries exceeded");
                        }));
    }

    public Flux<Map.Entry<String, String>> getNameOfOwnerAndNameOfPets() throws FileNotFoundException {
        PrintStream fileOutputStream = new PrintStream(new FileOutputStream("./src/main/java/com/example/client/files/exercise9"));
        return webClient.get()
                .uri("/owners")
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .flatMap(owner -> {
                    String ownerId = owner.get("id").asText();
                    String ownerName = owner.get("name").asText();
                    return webClient.get()
                            .uri("/pets")
                            .retrieve()
                            .bodyToFlux(JsonNode.class)
                            .filter(pet -> ownerId.equals(pet.get("ownerid").asText()))
                            .map(petsFiltered -> Map.entry(ownerName, petsFiltered.get("name").asText()));
                })
                .sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .doOnNext(output->{
                    String outputText = "Name of the pet: "+output.getValue() + "\tNames of the owner: "+output.getKey();
                    fileOutputStream.println(outputText);
                })
                .doFinally(signal -> fileOutputStream.close())
                .retryWhen(Retry
                        .max(3)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            throw new RuntimeException("Max retries exceeded");
                        }));
    }
}
