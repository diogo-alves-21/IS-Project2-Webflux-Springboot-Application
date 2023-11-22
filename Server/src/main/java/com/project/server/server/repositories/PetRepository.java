package com.project.server.server.repositories;

import com.project.server.server.models.Pet;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PetRepository extends R2dbcRepository<Pet,Integer> {

    Flux<Pet> findByOwnerid(int owner_id);

}

