package com.suspensive.store.services;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.suspensive.store.models.dto.AuthLoginDTO;
import com.suspensive.store.models.dto.AuthResponseDTO;
import com.suspensive.store.models.dto.AuthSignUpUserDTO;
import com.suspensive.store.models.dto.InvoiceDTO;
import com.suspensive.store.models.entities.AddressEntity;
import com.suspensive.store.models.entities.ProductCartEntity;
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
    List<ProductCartEntity> getCartProducts();
    ProductCartEntity addProductToCart(Long productId,int quantity) throws ProductNotFoundException,PremiumProductException ;
    ProductCartEntity editCartProduct(Long productCartId, int quantity) throws ProductNotFoundException;
    ProductCartEntity deleteCartProduct(Long productCartId) throws UsernameNotFoundException,ProductNotFoundException;

    void cleanUpCartItems();
    InvoiceDTO purchaseCart(Long addressId) throws AddressNotFoundException,InsufficientMoneyException;
}
