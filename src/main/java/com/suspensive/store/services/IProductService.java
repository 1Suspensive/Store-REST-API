package com.suspensive.store.services;

import java.util.List;

import com.suspensive.store.models.entities.ProductEntity;
import com.suspensive.store.models.exceptions.ProductNotFoundException;

public interface IProductService {
    ProductEntity addProduct(ProductEntity product);
    List<ProductEntity> addProducts(List<ProductEntity> products);
    List<ProductEntity> getProducts();
    List<ProductEntity> getProducts(String category);
    ProductEntity editProduct(Long productId,ProductEntity productModified) throws ProductNotFoundException;
    void deleteProduct(Long productId);
}
