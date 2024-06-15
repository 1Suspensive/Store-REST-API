package com.suspensive.store.services.interfaces;

import com.suspensive.store.models.entities.AddressEntity;
import com.suspensive.store.models.exceptions.AddressNotFoundException;

import java.util.Set;

public interface IAddressService {
    Set<AddressEntity> getAddresses();
    AddressEntity addAddress(AddressEntity address);
    AddressEntity deleteAddress(Long addressId) throws AddressNotFoundException;
    AddressEntity editAddress(AddressEntity newAddress, Long addressId) throws AddressNotFoundException;

}
