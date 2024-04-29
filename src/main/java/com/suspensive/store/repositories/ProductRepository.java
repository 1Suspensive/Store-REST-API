package com.suspensive.store.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.suspensive.store.models.entities.ProductEntity;

@Repository
public interface ProductRepository extends CrudRepository<ProductEntity,Long>{

}
