package com.suspensive.store.models.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.suspensive.store.models.entities.AddressEntity;
import com.suspensive.store.models.entities.ProductEntity;

@JsonPropertyOrder({"id","username","email","phoneNumber","address","cart","taxes","totalCost"})
public record InvoiceDTO(String username,
                         String email,
                         String phoneNumber,
                         AddressEntity address,
                         List<ProductEntity> cart,
                         String taxes,
                         double totalCost) {    

}
