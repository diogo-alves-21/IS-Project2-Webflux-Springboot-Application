package com.project.server.server.service;

import com.project.server.server.models.Pet;
import com.project.server.server.repositories.OwnerRepository;
import com.project.server.server.repositories.PetRepository;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class PetService {

    @Autowired
    PetRepository petRepository;

    private static Logger logger = LogManager.getLogger(OwnerService.class);

    public Flux<Pet> findAll(){
        logger.log(Level.INFO,"Pets were found with success");
        return petRepository.findAll();
    }

    public Mono<Pet> findById(int id){
        logger.log(Level.INFO,"Pet with the id "+id+" was found with success");
        return petRepository.findById(id);
    }

    public Mono<Pet> create(Pet pet){
        logger.log(Level.INFO,"Pet created with success");
        return petRepository.save(pet);
    }

    public Mono<Pet> update(int id, Pet pet){
        return petRepository.findById(id)
                .map(Optional::of).defaultIfEmpty(Optional.empty())
                .flatMap(updatePet ->{
                    if (updatePet.isPresent()){
                        Pet oldpet = updatePet.get();
                        if(pet.getName()==null){
                            pet.setName(oldpet.getName());
                        }
                        if (pet.getSpecie()==null){
                            pet.setSpecie(oldpet.getSpecie());
                        }
                        if (pet.getBirth_date()==null){
                            pet.setBirth_date(oldpet.getBirth_date());
                        }
                        if (pet.getWeight()==0){
                            pet.setWeight(oldpet.getWeight());
                        }
                        if (pet.getOwnerid()==0){
                            pet.setOwnerid(oldpet.getOwnerid());
                        }
                        pet.setId(id);
                        logger.log(Level.INFO,"Pet updated with success");
                        return petRepository.save(pet);
                    }
                    logger.log(Level.ERROR,"Failed to update pet");
                    return Mono.empty();
                });
    }

    public Mono<Void> delete(int id){
        logger.log(Level.INFO,"Pet with id "+id+" was deleted with success");
        return petRepository.deleteById(id);
    }

}
