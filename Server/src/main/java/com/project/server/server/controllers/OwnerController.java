package com.project.server.server.controllers;

import com.project.server.server.models.Owner;
import com.project.server.server.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RestController
@RequestMapping("owners")
public class OwnerController {

    @Autowired
    OwnerService ownerService;

    @PostMapping
    private Mono<Owner> createOwner(@RequestBody Owner owner){
        return ownerService.create(owner);
    }

    @GetMapping("/{id}")
    private Mono<Owner> getOwner(@PathVariable(name = "id") int id){

        return ownerService.findById(id);
    }

    @GetMapping
    private Flux<Owner> getAllOwners(){

        return ownerService.findAll();
    }

    @PutMapping("/{id}")
    private Mono<Owner> updateOwner(@PathVariable int id, @RequestBody Owner owner){

        return ownerService.update(id,owner);
    }

    @DeleteMapping("/{id}")
    private Mono<Void> deleteOwner(@PathVariable int id){

        return ownerService.delete(id);
    }
}
