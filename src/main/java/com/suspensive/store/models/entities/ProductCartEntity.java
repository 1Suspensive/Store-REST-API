package com.suspensive.store.models.entities;
import com.suspensive.store.models.interfaces.IPrototype;
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

public class ProductCartEntity implements IPrototype<ProductCartEntity> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.MERGE)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    private int quantity;

    @Override
    public ProductCartEntity clone() {
        return new ProductCartEntity(null, product, quantity);
    }
}
