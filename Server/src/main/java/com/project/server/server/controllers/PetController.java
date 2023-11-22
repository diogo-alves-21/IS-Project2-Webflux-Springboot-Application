package com.project.server.server.controllers;

import com.project.server.server.models.Pet;
import com.project.server.server.service.PetService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RestController
@RequestMapping("pets")
public class PetController {

    @Autowired
    PetService petService;

    @PostMapping
    private Mono<Pet> createPet(@RequestBody Pet pet){
        return petService.create(pet);
    }

    @GetMapping("/{id}")
    private Mono<Pet> getPet(@PathVariable int id){

        return petService.findById(id);
    }

    @GetMapping
    private Flux<Pet> getAllPets(){

        return petService.findAll();
    }

    @PutMapping("/{id}")
    private Mono<Pet> updatePet(@PathVariable int id, @RequestBody Pet pet){

        return petService.update(id,pet);
    }

    @DeleteMapping("/{id}")
    private Mono<Void> deletePet(@PathVariable int id){

        return petService.delete(id);
    }
}
