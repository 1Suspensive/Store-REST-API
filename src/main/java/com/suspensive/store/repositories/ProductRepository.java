package com.suspensive.store.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.suspensive.store.models.entities.ProductEntity;
import java.util.List;


@Repository
public interface ProductRepository extends CrudRepository<ProductEntity,Long>{
    List<ProductEntity> findByCategory(String category);
}
