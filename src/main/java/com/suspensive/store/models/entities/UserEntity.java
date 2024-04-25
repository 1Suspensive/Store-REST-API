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

    private String username;

    private String password;

    @Column(name = "phone_number")
    @Size(max=15)
    private String phoneNumber;

    private double wallet;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinTable(name = "user_adresses",joinColumns = @JoinColumn(name="user_id"), inverseJoinColumns = @JoinColumn(name="address_id"))
    private Set<AddressEntity> addresses;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_cart", joinColumns = @JoinColumn(name="user_id"),inverseJoinColumns = @JoinColumn(name="product_id"))
    private List<ProductEntity> cart;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name="user_id"),inverseJoinColumns = @JoinColumn(name="role_id"))
    private Set<RoleEntity> roles;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "account_no_expired")
    private boolean accountNoExpired;

    @Column(name = "account_no_locked")
    private boolean accountNoLocked;

    @Column(name = "credential_no_expired")
    private boolean credentialNoExpired;
}
