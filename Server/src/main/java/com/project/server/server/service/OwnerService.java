package com.project.server.server.service;

import com.project.server.server.models.Owner;
import com.project.server.server.repositories.OwnerRepository;
import com.project.server.server.repositories.PetRepository;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.apache.logging.log4j.Logger;


import java.util.Optional;

@Service
public class OwnerService {

    @Autowired
    OwnerRepository ownerRepository;

    @Autowired
    PetRepository petRepository;

    private static Logger logger = LogManager.getLogger(OwnerService.class);

    public Flux<Owner> findAll(){
        logger.log(Level.INFO,"Owners were found with success");
        return ownerRepository.findAll();
    }

    public Mono<Owner> findById(int id){
        logger.log(Level.INFO,"Owner with the id "+id+" was found with success");
        return ownerRepository.findById(id);
    }

    public Mono<Owner> create(Owner owner){
        logger.log(Level.INFO,"Owner created with success");
        return ownerRepository.save(owner);
    }

    public Mono<Owner> update(int id, Owner owner){
        return ownerRepository.findById(id)
                .map(Optional::of).defaultIfEmpty(Optional.empty())
                .flatMap(updateOwner ->{
                    if (updateOwner.isPresent()){
                        Owner oldOwner = updateOwner.get();
                        if (owner.getName() == null){
                            owner.setName(oldOwner.getName());
                        }
                        if (owner.getNumber() == 0){
                            owner.setNumber(oldOwner.getNumber());
                        }
                        owner.setId(id);
                        logger.log(Level.INFO,"Owner updated with success");
                        return ownerRepository.save(owner);
                    }
                    logger.log(Level.ERROR,"Failed to update owner");
                    return Mono.empty();
                });
    }


    public Mono<Void> delete(int ownerId) {
        return ownerRepository.findById(ownerId)
                .flatMap(owner -> petRepository.findByOwnerid(ownerId)
                        .count()
                        .flatMap(count -> {
                            if (count == 0) {
                                logger.log(Level.INFO,"Owner with id "+ownerId+" was deleted with success");
                                return ownerRepository.deleteById(ownerId);
                            } else {
                                logger.log(Level.ERROR,"Failed to delete owner");
                                return Mono.error(new Exception("Owner has pets and cannot be deleted."));
                            }
                        }));
    }

}
