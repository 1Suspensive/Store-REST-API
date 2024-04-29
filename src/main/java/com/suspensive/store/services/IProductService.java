package com.suspensive.store.services;

import java.util.List;

import com.suspensive.store.models.entities.ProductEntity;

public interface IProductService {
    ProductEntity addProduct(ProductEntity product);
    List<ProductEntity> addProducts(List<ProductEntity> products);

}
