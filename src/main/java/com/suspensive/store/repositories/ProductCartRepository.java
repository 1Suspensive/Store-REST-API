package com.suspensive.store.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.suspensive.store.models.entities.ProductCartEntity;

@Repository
public interface ProductCartRepository extends CrudRepository<ProductCartEntity,Long>{
}
