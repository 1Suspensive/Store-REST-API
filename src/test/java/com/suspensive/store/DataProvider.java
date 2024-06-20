package com.suspensive.store;

import com.suspensive.store.models.entities.*;

import java.util.*;

public class DataProvider {

    //Products

    public static ProductEntity productMock(){
        return ProductEntity.builder()
                .id(1L)
                .name("Charger")
                .price(20)
                .category("electronics")
                .stock(1)
                .premium(false)
                .build();
    }

    public static ProductEntity newProductMock(){
        return ProductEntity.builder()
                .id(1L)
                .name("Charger")
                .price(25)
                .category("electronics")
                .stock(5)
                .premium(true)
                .build();
    }

    public static List<ProductEntity> productsMock(){
        return List.of(
                new ProductEntity(1L,"Charger",20,"electronics",1,false),
                new ProductEntity(2L,"Table",50,"home",100,true)
        );
    }

    public static List<ProductEntity> productsMockByCategory(){
        return List.of(
                new ProductEntity(2L,"Table",50,"home",100,true),
                new ProductEntity(3L,"Bed",100,"home",10,false)
        );
    }

    //User

    public static UserEntity userMock(){
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(new RoleEntity(1L, RolesEnum.DEFAULT_USER,Set.of(
                new PermissionEntity(1L,"READ"),
                new PermissionEntity(2L,"DEFAULT_PURCHASE")
        )));

        Set<InvoiceEntity> invoices = new HashSet<>();
        invoices.add(InvoiceEntity.builder()
                .id(1L)
                .user(null)
                .address(AddressEntity.builder()
                        .id(1L)
                        .country("Colombia")
                        .address("cra 33")
                        .city("Medellin")
                        .zipCode("1234").build())
                .totalCost(1000)
                .cart(List.of(
                        ProductCartEntity.builder()
                                .id(1L)
                                .product(DataProvider.productMock())
                                .quantity(2)
                                .build()
                ))
                .build());


        return UserEntity.builder()
                .id(1L)
                .username("jeferson")
                .password("12345")
                .email("jeferson@gmail.com")
                .phoneNumber("3008317482")
                .wallet(100d)
                .roles(roles)
                .invoices(invoices)
                .addresses(new HashSet<>())
                .cart(new ArrayList<>())
                .isEnabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .build();
    }

    public static UserEntity userMockWithCart(){
        UserEntity user = DataProvider.userMock();
        user.getCart().add(ProductCartEntity.builder()
                .id(1L)
                .quantity(2)
                .product(DataProvider.productMock()).build());
        user.getAddresses().add(AddressEntity.builder()
                .id(1L)
                .country("Colombia")
                .address("cra 33")
                .city("Medellin")
                .zipCode("1234").build());
        return user;
    }

    public static UserEntity userPremiumMock(){
        UserEntity user = DataProvider.userMockWithCart();
        user.getRoles().add(new RoleEntity(2L, RolesEnum.PREMIUM_USER,Set.of(
                new PermissionEntity(1L,"READ"),
                new PermissionEntity(2L,"DEFAULT_PURCHASE"),
                new PermissionEntity(3L,"PREMIUM_PURCHASE")
        )));
        return user;
    }


    //Role

    public static RoleEntity premiumRoleMock(){
        return RoleEntity.builder()
                .id(2L)
                .rolesEnum(RolesEnum.PREMIUM_USER).permissions(Set.of(
                PermissionEntity.builder().id(1L).name("READ").build(),
                PermissionEntity.builder().id(2L).name("DEFAULT_PURCHASE").build(),
                PermissionEntity.builder().id(3L).name("PREMIUM_PURCHASE").build()
        )).build();
    }

    //Product Cart

    public static ProductCartEntity productCartMock(){
        return ProductCartEntity.builder()
                        .id(1L)
                        .quantity(2)
                        .product(DataProvider.productMock())
                        .build();
    }

    public static ProductCartEntity newProductCartMock(){
        return ProductCartEntity.builder()
                .id(1L)
                .quantity(3)
                .product(DataProvider.productMock())
                .build();
    }

    //Address
    public static AddressEntity addressMock(){
        return AddressEntity.builder()
                .id(1L)
                .country("Colombia")
                .address("cra 33")
                .city("Medellin")
                .zipCode("1234").build();
    }

    public static AddressEntity newAddressMock(){
        return AddressEntity.builder()
                .id(1L)
                .country("USA")
                .address("Street 34")
                .city("Seattle")
                .zipCode("1234").build();
    }
}
