package com.suspensive.store.models.entities;

import java.util.List;
import java.util.Set;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @Column(name = "phone_number")
    @Size(max=15,message = "Number is not valid")
    private String phoneNumber;

    private double wallet;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.REMOVE)
    @JoinTable(name = "user_adresses",joinColumns = @JoinColumn(name="user_id"), inverseJoinColumns = @JoinColumn(name="address_id"))
    private Set<AddressEntity> addresses;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE,CascadeType.MERGE})
    @JoinTable(name = "user_cart", joinColumns = @JoinColumn(name="user_id"),inverseJoinColumns = @JoinColumn(name="product_cart_id"))
    private List<ProductCartEntity> cart;
 
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name="user_id"),inverseJoinColumns = @JoinColumn(name="role_id"))
    private Set<RoleEntity> roles;

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Set<InvoiceEntity> invoices;
    
    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "account_no_expired")
    private boolean accountNoExpired;

    @Column(name = "account_no_locked")
    private boolean accountNoLocked;

    @Column(name = "credential_no_expired")
    private boolean credentialNoExpired;
}
