package com.suspensive.store.services;

import com.suspensive.store.models.dto.AuthLoginDTO;
import com.suspensive.store.models.dto.AuthResponseDTO;
import com.suspensive.store.models.dto.AuthSignUpUserDTO;
import com.suspensive.store.models.entities.ProductEntity;
import com.suspensive.store.models.exceptions.ProductNotFoundException;

public interface IUserService {
    AuthResponseDTO createUser(AuthSignUpUserDTO user) throws Exception;
    AuthResponseDTO login(AuthLoginDTO user);
    ProductEntity addProductToCart(Long productId) throws ProductNotFoundException;
    ProductEntity deleteCartProduct(Long productId) throws ProductNotFoundException;
}
