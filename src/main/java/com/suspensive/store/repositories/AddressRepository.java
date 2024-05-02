package com.suspensive.store.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.suspensive.store.models.entities.AddressEntity;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity,Long>{

}
