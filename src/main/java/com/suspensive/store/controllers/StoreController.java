package com.suspensive.store.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.suspensive.store.models.dto.BasicResponseDTO;
import com.suspensive.store.models.exceptions.ProductNotFoundException;
import com.suspensive.store.services.IUserService;


@RestController
public class StoreController {

    @Autowired
    private IUserService userService;

    @PatchMapping("cart/add/{productId}")
    public ResponseEntity<BasicResponseDTO> addProductToCart(@PathVariable Long productId) throws ProductNotFoundException{
        BasicResponseDTO response = new BasicResponseDTO("Product added to cart.", userService.addProductToCart(productId));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PatchMapping("cart/delete/{productId}")
    public ResponseEntity<BasicResponseDTO> deleteCartProduct(@PathVariable Long productId) throws ProductNotFoundException{
        BasicResponseDTO response = new BasicResponseDTO("Product deleted from cart.", deleteCartProduct(productId));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
