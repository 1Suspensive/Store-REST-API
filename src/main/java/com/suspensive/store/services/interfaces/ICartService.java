package com.suspensive.store.services.interfaces;

import com.suspensive.store.models.dto.InvoiceDTO;
import com.suspensive.store.models.entities.ProductCartEntity;
import com.suspensive.store.models.exceptions.AddressNotFoundException;
import com.suspensive.store.models.exceptions.InsufficientMoneyException;
import com.suspensive.store.models.exceptions.PremiumProductException;
import com.suspensive.store.models.exceptions.ProductNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface ICartService {
    List<ProductCartEntity> getCartProducts();
    ProductCartEntity addProductToCart(Long productId,int quantity) throws ProductNotFoundException, PremiumProductException;
    ProductCartEntity editCartProduct(Long productCartId, int quantity) throws ProductNotFoundException;
    List<ProductCartEntity> deleteCartProduct(Long productCartId) throws UsernameNotFoundException,ProductNotFoundException;

    List<ProductCartEntity> cleanUpCartItems();
    InvoiceDTO purchaseCart(Long addressId) throws AddressNotFoundException, InsufficientMoneyException;
}
