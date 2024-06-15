package com.suspensive.store.services.interfaces;

import java.util.List;
import java.util.Set;

import com.suspensive.store.models.entities.InvoiceEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.suspensive.store.models.dto.AuthLoginDTO;
import com.suspensive.store.models.dto.AuthResponseDTO;
import com.suspensive.store.models.dto.AuthSignUpUserDTO;
import com.suspensive.store.models.dto.InvoiceDTO;
import com.suspensive.store.models.entities.ProductCartEntity;
import com.suspensive.store.models.exceptions.AddressNotFoundException;
import com.suspensive.store.models.exceptions.InsufficientMoneyException;
import com.suspensive.store.models.exceptions.PremiumProductException;
import com.suspensive.store.models.exceptions.ProductNotFoundException;

public interface IUserService {
    AuthResponseDTO createUser(AuthSignUpUserDTO user) throws Exception;
    AuthResponseDTO login(AuthLoginDTO user);

    Set<InvoiceDTO> getInvoices();
}
