package com.suspensive.store.services;

import java.util.List;

import com.suspensive.store.services.interfaces.IProductService;
import org.springframework.stereotype.Service;

import com.suspensive.store.models.entities.ProductEntity;
import com.suspensive.store.models.exceptions.ProductNotFoundException;
import com.suspensive.store.repositories.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

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
        ProductEntity product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
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
    @Transactional
    public void deleteProduct(Long productId) throws ProductNotFoundException {
        //This line tests if product exists
        productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        productRepository.deleteById(productId);
    }

}
