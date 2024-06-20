package com.suspensive.store.services;

import com.suspensive.store.DataProvider;
import com.suspensive.store.models.dto.InvoiceDTO;
import com.suspensive.store.models.entities.AddressEntity;
import com.suspensive.store.models.entities.ProductCartEntity;
import com.suspensive.store.models.entities.RolesEnum;
import com.suspensive.store.models.entities.UserEntity;
import com.suspensive.store.models.exceptions.AddressNotFoundException;
import com.suspensive.store.models.exceptions.InsufficientMoneyException;
import com.suspensive.store.models.exceptions.PremiumProductException;
import com.suspensive.store.models.exceptions.ProductNotFoundException;
import com.suspensive.store.repositories.*;
import static org.junit.jupiter.api.Assertions.*;

import com.suspensive.store.services.interfaces.IEmailService;
import com.suspensive.store.util.JwtUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ProductCartRepository productCartRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private IEmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;


    @BeforeAll
    static void setup(){
        UserEntity user = DataProvider.userMock();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRolesEnum().name()))));
        user.getRoles().stream().flatMap(role -> role.getPermissions().stream())
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));
        User userDetails = new User(user.getUsername(),user.getPassword(),user.isEnabled(),user.isAccountNoExpired(),
                user.isCredentialNoExpired(),
                user.isAccountNoLocked(),authorities);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(),userDetails.getPassword(),userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testGetInvoices(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMock()));

        Set<InvoiceDTO> invoices = userService.getInvoices();

        verify(userRepository).findUserEntityByUsername(anyString());
        assertFalse(invoices.isEmpty());
    }

    @Test
    void testGetInvoicesWithUserNotFound(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,()->userService.getInvoices());
    }

    @Test
    void testAddProductToCart() throws PremiumProductException, ProductNotFoundException {
        Long productId = 1L;
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMock()));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.productMock()));
        when(userRepository.save(any(UserEntity.class))).thenReturn(DataProvider.userMockWithCart());

        ProductCartEntity result = userService.addProductToCart(productId,2);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).findUserEntityByUsername(anyString());
        verify(productRepository).findById(idCaptor.capture());
        verify(userRepository).save(any(UserEntity.class));

        assertEquals(productId,idCaptor.getValue());
        assertNotNull(result);
        assertEquals(2,result.getQuantity());
    }

    @Test
    void testAddProductToCartWithUserNotFound(){
        Long productId = 1L;
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,()->userService.addProductToCart(productId,2));
    }

    @Test
    void testAddProductNotFoundToCart(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMock()));
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,()-> userService.addProductToCart(1L,2));
    }

    @Test
    void testAddPremiumProductToCartException(){
        Long productId = 1L;
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMock()));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.newProductMock()));
        when(roleRepository.findRoleEntityByRolesEnum(any(RolesEnum.class))).thenReturn(DataProvider.premiumRoleMock());

        assertThrows(PremiumProductException.class,()->userService.addProductToCart(productId,2));
    }

    @Test
    void testEditCartProduct() throws ProductNotFoundException {
        Long productCartId = 1L;
        when(productCartRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.productCartMock()));
        when(productCartRepository.save(any(ProductCartEntity.class))).thenReturn(DataProvider.newProductCartMock());

       ProductCartEntity result = userService.editCartProduct(productCartId,3);

       ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
       verify(productCartRepository).findById(idCaptor.capture());
       verify(productCartRepository).save(any(ProductCartEntity.class));
       assertEquals(productCartId,idCaptor.getValue());
       assertNotNull(result);
       assertEquals(3,result.getQuantity());
    }

    @Test
    void testEditCartProductWithProductNotFound(){
        Long productCartId = 1L;
        when(productCartRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,()->userService.editCartProduct(productCartId,3));
    }

    @Test
    void testDeleteCartProduct() throws ProductNotFoundException {
        Long productCartId = 1L;
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMock()));
        when(productCartRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.productCartMock()));
        when(userRepository.save(any(UserEntity.class))).thenReturn(DataProvider.userMock());

        List<ProductCartEntity> result = userService.deleteCartProduct(productCartId);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).findUserEntityByUsername(anyString());
        verify(productCartRepository).findById(idCaptor.capture());
        verify(userRepository).save(any(UserEntity.class));
        assertEquals(productCartId,idCaptor.getValue());
        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteCartProductWithUserNotFound(){
        Long productCartId = 1L;
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,()->userService.deleteCartProduct(productCartId));
    }

    @Test
    void testDeleteCartProductWithProductNotFound(){
        Long productCartId = 1L;
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMock()));
        when(productCartRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,()->userService.deleteCartProduct(productCartId));
    }

    @Test
    void testCleanUpCartItems(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMockWithCart()));
        when(userRepository.save(any(UserEntity.class))).thenReturn(DataProvider.userMock());

        List<ProductCartEntity> result = userService.cleanUpCartItems();

        verify(userRepository).findUserEntityByUsername(anyString());
        verify(userRepository).save(any(UserEntity.class));
        assertTrue(result.isEmpty());
    }

    @Test
    void testCleanUpCartItemsWithUserNotFound(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,()->userService.cleanUpCartItems());
    }

     @Test
    void testPurchaseCart() throws AddressNotFoundException, InsufficientMoneyException {
        Long addressId = 1L;
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMockWithCart()));
        when(addressRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.addressMock()));

        InvoiceDTO result = userService.purchaseCart(addressId);

        ArgumentCaptor<Long> addressIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).findUserEntityByUsername(anyString());
        verify(addressRepository).findById(addressIdCaptor.capture());
        assertEquals(addressId,addressIdCaptor.getValue());
        assertNotNull(result);
        assertEquals(47.6,result.totalCost());
        assertEquals("jeferson",result.username());
    }

    @Test
    void testPurchaseCartWithUserNotFound(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,()->userService.purchaseCart(1L));
    }

    @Test
    void testPurchaseCartWithAddressNotFoundOnAddressRepository(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMockWithCart()));
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class,()->userService.purchaseCart(1L));
    }

    @Test
    void testPurchaseCartWithAddressNotFoundOnUserAddresses(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMockWithCart()));
        when(addressRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.newAddressMock()));

        assertThrows(AddressNotFoundException.class,()->userService.purchaseCart(1L));
    }

    @Test
    void testPurchaseCartWithUserPremium() throws AddressNotFoundException, InsufficientMoneyException {
        Long addressId = 1L;
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userPremiumMock()));
        when(addressRepository.findById(anyLong())).thenReturn(Optional.of(DataProvider.addressMock()));
        when(roleRepository.findRoleEntityByRolesEnum(any(RolesEnum.class))).thenReturn(DataProvider.premiumRoleMock());

        InvoiceDTO result = userService.purchaseCart(addressId);

        ArgumentCaptor<Long> addressIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).findUserEntityByUsername(anyString());
        verify(addressRepository).findById(addressIdCaptor.capture());
        assertEquals(addressId,addressIdCaptor.getValue());
        assertNotNull(result);
        assertEquals(42.84,result.totalCost());
        assertEquals("jeferson",result.username());
    }

    @Test
    void testDeleteCartProductsFromRepository(){
        List<ProductCartEntity> productsOnCart = List.of(DataProvider.productCartMock());
        doNothing().when(productCartRepository).deleteAllById(anyList());

        userService.deleteCartProductsFromRepository(productsOnCart);

        verify(productCartRepository).deleteAllById(anyList());
    }

    @Test
    void testGetAddresses(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMockWithCart()));

        Set<AddressEntity> result = userService.getAddresses();

        verify(userRepository).findUserEntityByUsername(anyString());
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetAddressesWithUserNotFound(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,()->userService.getAddresses());
    }

    @Test
    void testAddAddress(){
        AddressEntity address = DataProvider.addressMock();
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMockWithCart()));
        when(userRepository.save(any(UserEntity.class))).thenReturn(DataProvider.userMockWithCart());

        Set<AddressEntity> result = userService.addAddress(address);

        verify(userRepository).findUserEntityByUsername(anyString());
        verify(userRepository).save(any(UserEntity.class));
        assertFalse(result.isEmpty());
        assertEquals(address,result.iterator().next());
    }

    @Test
    void testAddAddressWithUserNotFound(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,()->userService.addAddress(DataProvider.addressMock()));
    }

    @Test
    void testDeleteAddress() throws AddressNotFoundException {
        Long addressId = 1L;
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMockWithCart()));
        when(addressRepository.findById(anyLong())).thenReturn(Optional.ofNullable(DataProvider.addressMock()));
        when(userRepository.save(any(UserEntity.class))).thenReturn(DataProvider.userMock());

        Set<AddressEntity> result = userService.deleteAddress(addressId);

        ArgumentCaptor<Long> addressIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).findUserEntityByUsername(anyString());
        verify(addressRepository).findById(addressIdCaptor.capture());
        verify(userRepository).save(any(UserEntity.class));
        assertEquals(addressId,addressIdCaptor.getValue());
        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteAddressWithUserNotFound(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,()->userService.deleteAddress(1L));
    }

    @Test
    void testDeleteAddressWithAddressNotFound(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMockWithCart()));
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class,()->userService.deleteAddress(1L));
    }

    @Test
    void testEditAddress() throws AddressNotFoundException {
        Long addressId = 1L;
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMockWithCart()));
        when(userRepository.save(any(UserEntity.class))).thenReturn(DataProvider.userMockWithCart());

        Set<AddressEntity> result = userService.editAddress(DataProvider.newAddressMock(),addressId);

        verify(userRepository).findUserEntityByUsername(anyString());
        verify(userRepository).save(any(UserEntity.class));
        assertEquals(1L,result.iterator().next().getId());
        assertFalse(result.isEmpty());
        assertEquals("Colombia",result.iterator().next().getCountry());
    }

    @Test
    void testEditAddressWithUserNotFound(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,()->userService.editAddress(DataProvider.newAddressMock(),1L));
    }

    @Test
    void testEditAddressWithAddressNotFound(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMock()));

        assertThrows(AddressNotFoundException.class,()->userService.editAddress(DataProvider.addressMock(),1L));
    }

    @Test
    void testEditAddressWithAddressNotFoundBecauseAddressId(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMockWithCart()));

        assertThrows(AddressNotFoundException.class,()->userService.editAddress(DataProvider.newAddressMock(),2L));
    }

    @Test
    void testGetCartProducts(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.of(DataProvider.userMockWithCart()));

        List<ProductCartEntity> result = userService.getCartProducts();

        verify(userRepository).findUserEntityByUsername(anyString());
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetCartProductsWithUserNotFound(){
        when(userRepository.findUserEntityByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,()->userService.getCartProducts());
    }
}
