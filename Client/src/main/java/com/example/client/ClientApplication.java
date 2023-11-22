package com.example.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

@SpringBootApplication
public class ClientApplication {

	static WebClient.Builder webClientBuilder() {
		return WebClient.builder().baseUrl("http://localhost:8080");
	}

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);

		OwnerService ownerService = new OwnerService(webClientBuilder());
		PetService petService = new PetService(webClientBuilder());

		//Exercise 1
		try {
			ownerService.getAllOwnersNamesAndTelephones();
			Thread.sleep(1000);
		}catch (InterruptedException | FileNotFoundException e){
			e.printStackTrace();
		}

        //Exercise 2
		try {
			petService.getTotalOfPets();
			Thread.sleep(1000);
		}catch (InterruptedException | FileNotFoundException e){
			e.printStackTrace();
		}

		//Exercise 3
		try {
			petService.getTotalOfDogs();
			Thread.sleep(1000);
		}catch (InterruptedException | FileNotFoundException e){
			e.printStackTrace();
		}

		//Exercise 4
		try {
			petService.getTotalPetsWithMoreThan10Kg();
			Thread.sleep(1000);
		}catch (InterruptedException | FileNotFoundException e){
			e.printStackTrace();
		}

		//Exercise 5
		try {
			petService.getAverageAndDeviationOfWeights();
			Thread.sleep(1000);
		}catch (InterruptedException | FileNotFoundException e){
			e.printStackTrace();
		}

		//Exercise 6
		try {
			petService.getNameOfTheEldestPet();
			Thread.sleep(1000);
		}catch (InterruptedException | FileNotFoundException e){
			e.printStackTrace();
		}

		//Exercise 7
		try {
			petService.getAveragePetsPerOwner();
			Thread.sleep(1000);
		}catch (InterruptedException | FileNotFoundException e){
			e.printStackTrace();
		}

		//Exercise 8
		try {
			ownerService.getNameOfOwnerAndNumberOfPets()
					.subscribe(iterable -> System.out.println("Name: "+iterable.getKey() + "\tNumber of pets: "+iterable.getValue()));
			Thread.sleep(1000);
		}catch (InterruptedException | FileNotFoundException e){
			e.printStackTrace();
		}

		//Exercise 9
		try {
			ownerService.getNameOfOwnerAndNameOfPets()
					.subscribe(iterable -> System.out.println("\nName of the pet: "+iterable.getValue() + "\tNames of the owner: "+iterable.getKey()));
			Thread.sleep(1000);
		}catch (InterruptedException | FileNotFoundException e){
			e.printStackTrace();
		}
	}
}
