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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToOne(fetch=FetchType.EAGER,targetEntity = AddressEntity.class)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    private final double taxes = 0.19;
    
    @Column(name = "total_cost")
    private double totalCost;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "invoice_cart", joinColumns = @JoinColumn(name="invoice_id"),inverseJoinColumns = @JoinColumn(name="product_id"))
    private List<ProductEntity> cart;
}
