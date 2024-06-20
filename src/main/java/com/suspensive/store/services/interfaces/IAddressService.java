package com.suspensive.store.services.interfaces;

import com.suspensive.store.models.entities.AddressEntity;
import com.suspensive.store.models.exceptions.AddressNotFoundException;

import java.util.Set;

public interface IAddressService {
    Set<AddressEntity> getAddresses();
    Set<AddressEntity> addAddress(AddressEntity address);
    Set<AddressEntity> deleteAddress(Long addressId) throws AddressNotFoundException;
    Set<AddressEntity> editAddress(AddressEntity newAddress, Long addressId) throws AddressNotFoundException;
}
