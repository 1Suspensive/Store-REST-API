package com.suspensive.store.models.entities;
import lombok.*;
import jakarta.persistence.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "products_cart")

public class ProductCartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    private int quantity;
}
