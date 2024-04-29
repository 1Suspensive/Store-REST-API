package com.suspensive.store.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suspensive.store.models.entities.ProductEntity;
import com.suspensive.store.repositories.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public ProductEntity addProduct(ProductEntity product) {
        return productRepository.save(product);
    }

    @Override
    public List<ProductEntity> addProducts(List<ProductEntity> products) {
        return (List<ProductEntity>) productRepository.saveAll(products);
    }

}
