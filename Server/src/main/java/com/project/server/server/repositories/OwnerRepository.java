package com.project.server.server.repositories;

import com.project.server.server.models.Owner;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepository extends R2dbcRepository<Owner,Integer> {
}
