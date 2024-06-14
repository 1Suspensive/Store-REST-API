package com.suspensive.store.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.suspensive.store.models.entities.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity,Long>{
    Optional<UserEntity> findUserEntityByUsername(String username);

}
