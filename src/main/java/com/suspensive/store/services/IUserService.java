package com.suspensive.store.services;

import java.util.Set;

import com.suspensive.store.models.dto.AuthLoginDTO;
import com.suspensive.store.models.dto.AuthResponseDTO;
import com.suspensive.store.models.dto.AuthSignUpUserDTO;
import com.suspensive.store.models.dto.InvoiceDTO;
import com.suspensive.store.models.entities.AddressEntity;
import com.suspensive.store.models.entities.ProductEntity;
import com.suspensive.store.models.exceptions.AddressNotFoundException;
import com.suspensive.store.models.exceptions.InsufficientMoneyException;
import com.suspensive.store.models.exceptions.PremiumProductException;
import com.suspensive.store.models.exceptions.ProductNotFoundException;

public interface IUserService {
    //Basic Authentication
    AuthResponseDTO createUser(AuthSignUpUserDTO user) throws Exception;
    AuthResponseDTO login(AuthLoginDTO user);

    //Adresses Service
    Set<AddressEntity> getAddresses();
    AddressEntity addAddress(AddressEntity address);
    AddressEntity deleteAddress(Long addressId) throws AddressNotFoundException;
    AddressEntity editAddress(AddressEntity newAddress, Long addressId) throws AddressNotFoundException;

    //Cart Services
    ProductEntity addProductToCart(Long productId) throws ProductNotFoundException,PremiumProductException ;
    ProductEntity deleteCartProduct(Long productId) throws ProductNotFoundException;
    void cleanUpCartItems();
    InvoiceDTO purchaseCart(Long addressId) throws AddressNotFoundException,InsufficientMoneyException;
}
