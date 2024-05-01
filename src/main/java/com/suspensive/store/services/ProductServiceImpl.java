package com.suspensive.store.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suspensive.store.models.entities.ProductEntity;
import com.suspensive.store.models.exceptions.ProductNotFoundException;
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
    @Transactional
    public List<ProductEntity> addProducts(List<ProductEntity> products) {
        return (List<ProductEntity>) productRepository.saveAll(products);
    }

    @Override
    public List<ProductEntity> getProducts() {
        return (List<ProductEntity>) productRepository.findAll();
    }

    @Override
    @Transactional
    public ProductEntity editProduct(Long productId, ProductEntity productModified) throws ProductNotFoundException {
        ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException());
        product.setName(productModified.getName());
        product.setPrice(productModified.getPrice());
        product.setStock(productModified.getStock());
        product.setCategory(productModified.getCategory());
        return productRepository.save(product);
    }

    @Override
    public List<ProductEntity> getProducts(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

}
