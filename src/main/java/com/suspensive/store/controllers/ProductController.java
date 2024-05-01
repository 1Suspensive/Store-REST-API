package com.suspensive.store.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.suspensive.store.models.dto.BasicResponseDTO;
import com.suspensive.store.models.entities.ProductEntity;
import com.suspensive.store.models.exceptions.ProductNotFoundException;
import com.suspensive.store.services.IProductService;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private IProductService productService;
    
    @GetMapping
    public List<ProductEntity> getProducts(){
        return productService.getProducts();
    }

    @GetMapping("/filter")
    public List<ProductEntity> getProductsByCategory(@RequestParam String category){
        return productService.getProducts(category);
    }

    @PostMapping("/add")
    public ResponseEntity<BasicResponseDTO> addProduct(@RequestBody ProductEntity product){
        BasicResponseDTO response = new BasicResponseDTO("Product added succesfully.", productService.addProduct(product));
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PostMapping("/add/productsList")
    public ResponseEntity<BasicResponseDTO> addProducts(@RequestBody List<ProductEntity> products){
        BasicResponseDTO response = new BasicResponseDTO("Products added succesfully.", productService.addProducts(products));
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PatchMapping("/edit/{productId}")
    public ResponseEntity<BasicResponseDTO> editProduct(@PathVariable Long productId, @RequestBody ProductEntity productModified) throws ProductNotFoundException{
        BasicResponseDTO response = new BasicResponseDTO("Product edited succesfully.", productService.editProduct(productId, productModified));        
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<BasicResponseDTO> deleteProduct(@PathVariable Long productId){
        productService.deleteProduct(productId);
        BasicResponseDTO response = new BasicResponseDTO("Product deleted succesfully.", null);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
