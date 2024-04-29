package com.suspensive.store.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.suspensive.store.models.dto.BasicResponseDTO;
import com.suspensive.store.models.entities.ProductEntity;
import com.suspensive.store.services.IProductService;

@RestController
public class StoreController {

    @Autowired
    private IProductService productService;

    @GetMapping("/hello")
    public String hello(){
        return "Hello WORLD";
    }

    @PostMapping("/add/product")
    public ResponseEntity<BasicResponseDTO> addProduct(@RequestBody ProductEntity product){
        BasicResponseDTO response = new BasicResponseDTO("Product added succesfully.", productService.addProduct(product));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/add/products")
    public ResponseEntity<BasicResponseDTO> addProducts(@RequestBody List<ProductEntity> products){
        BasicResponseDTO response = new BasicResponseDTO("Products added succesfully.", productService.addProducts(products));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
