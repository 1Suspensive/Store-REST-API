package com.suspensive.store.controllers;


import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.suspensive.store.models.dto.BasicResponseDTO;
import com.suspensive.store.models.entities.AddressEntity;
import com.suspensive.store.models.entities.ProductCartEntity;
import com.suspensive.store.models.exceptions.AddressNotFoundException;
import com.suspensive.store.models.exceptions.InsufficientMoneyException;
import com.suspensive.store.models.exceptions.PremiumProductException;
import com.suspensive.store.models.exceptions.ProductNotFoundException;
import com.suspensive.store.services.IUserService;


@RestController
public class StoreController {

    @Autowired
    private IUserService userService;

    @GetMapping("addresses")
    public Set<AddressEntity> getAddresses(){
        return userService.getAddresses();
    }

    @PatchMapping("addresses/add")
    public ResponseEntity<BasicResponseDTO> addAddress(@RequestBody AddressEntity address){
        BasicResponseDTO response = new BasicResponseDTO("Address added succesfully!", userService.addAddress(address));
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @PatchMapping("addresses/delete/{addressId}")
    public ResponseEntity<BasicResponseDTO> deleteAddress(@PathVariable Long addressId) throws AddressNotFoundException{
        BasicResponseDTO response = new BasicResponseDTO("Address deleted succesfully!", userService.deleteAddress(addressId));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PatchMapping("addresses/edit/{addressId}")
    public ResponseEntity<BasicResponseDTO> editAddress(@RequestBody AddressEntity address, @PathVariable Long addressId) throws AddressNotFoundException{
        BasicResponseDTO response = new BasicResponseDTO("Address edited succesfully!", userService.editAddress(address, addressId));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("cart")
    public List<ProductCartEntity> getCartProducts(){
        return userService.getCartProducts();
    }

    @PatchMapping("cart/add/{productId}")
    public ResponseEntity<BasicResponseDTO> addProductToCart(@PathVariable Long productId, @RequestParam int quantity) throws ProductNotFoundException, PremiumProductException{
        BasicResponseDTO response = new BasicResponseDTO("Product added to cart.", userService.addProductToCart(productId,quantity));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PatchMapping("cart/edit/{productId}")
    public ResponseEntity<BasicResponseDTO> editProductCart(@PathVariable Long productId, @RequestParam int quantity) throws ProductNotFoundException{
        BasicResponseDTO response = new BasicResponseDTO("Product edited succesfully.", userService.editCartProduct(productId,quantity));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PatchMapping("cart/delete/{productId}")
    public ResponseEntity<BasicResponseDTO> deleteCartProduct(@PathVariable Long productId) throws ProductNotFoundException{
        BasicResponseDTO response = new BasicResponseDTO("Product deleted from cart.", userService.deleteCartProduct(productId));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PatchMapping("cart/clean-up")
    public ResponseEntity<BasicResponseDTO> cleanUpCartItems(){
        userService.cleanUpCartItems();
        return new ResponseEntity<>(new BasicResponseDTO("Now your cart is empty!", null), HttpStatus.OK);
    }

    @PatchMapping("/cart/purchase")
    public ResponseEntity<BasicResponseDTO> purchaseCart(@RequestParam Long addressId) throws AddressNotFoundException, InsufficientMoneyException{
        BasicResponseDTO response = new BasicResponseDTO("Cart purchased correctly", userService.purchaseCart(addressId));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
