package com.suspensive.store.models.entities;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invoices")
public class InvoiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch=FetchType.EAGER,targetEntity = AddressEntity.class,cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    private final double taxes = 0.19;
    
    @Column(name = "total_cost")
    private double totalCost;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(name = "invoice_cart", joinColumns = @JoinColumn(name="invoice_id"),inverseJoinColumns = @JoinColumn(name="product_cart_id"))
    private List<ProductCartEntity> cart;
}
