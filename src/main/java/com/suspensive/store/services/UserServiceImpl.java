package com.suspensive.store.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.suspensive.store.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.suspensive.store.models.dto.AuthLoginDTO;
import com.suspensive.store.models.dto.AuthResponseDTO;
import com.suspensive.store.models.dto.AuthSignUpUserDTO;
import com.suspensive.store.models.dto.InvoiceDTO;
import com.suspensive.store.models.entities.AddressEntity;
import com.suspensive.store.models.entities.InvoiceEntity;
import com.suspensive.store.models.entities.ProductCartEntity;
import com.suspensive.store.models.entities.ProductEntity;
import com.suspensive.store.models.entities.RoleEntity;
import com.suspensive.store.models.entities.RolesEnum;
import com.suspensive.store.models.entities.UserEntity;
import com.suspensive.store.models.exceptions.AddressNotFoundException;
import com.suspensive.store.models.exceptions.InsufficientMoneyException;
import com.suspensive.store.models.exceptions.PremiumProductException;
import com.suspensive.store.models.exceptions.ProductNotFoundException;
import com.suspensive.store.util.JwtUtils;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements IUserService{

    
    private final UserRepository userRepository;
    
    private final RoleRepository roleRepository;
    
    private final ProductRepository productRepository;
    
    private final AddressRepository addressRepository;

    private final ProductCartRepository productCartRepository;

    private final JwtUtils jwtUtils;
    
    private final PasswordEncoder passwordEncoder;
    
    private final IEmailService emailService;
    
    private final UserDetailsService userDetailsService;

    private final InvoiceRepository invoiceRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, ProductRepository productRepository, AddressRepository addressRepository, ProductCartRepository productCartRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, IEmailService emailService, UserDetailsService userDetailsService, InvoiceRepository invoiceRepository){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.productRepository = productRepository;
        this.addressRepository = addressRepository;
        this.productCartRepository = productCartRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userDetailsService = userDetailsService;
        this.invoiceRepository = invoiceRepository;
    }
    
    @Override
    @Transactional(rollbackOn = Exception.class)
    public AuthResponseDTO createUser(AuthSignUpUserDTO user) throws Exception {

        emailService.validateEmail(user.email());

        Set<RoleEntity> defaultRole = Set.of(roleRepository.findRoleEntityByRolesEnum(RolesEnum.DEFAULT_USER));
        UserEntity newUser = UserEntity.builder()
                             .username(user.username())
                             .password(passwordEncoder.encode(user.password()))
                             .email(user.email())
                             .phoneNumber(user.phoneNumber())
                             .wallet(user.wallet())
                             .roles(defaultRole)
                             .isEnabled(true)
                             .accountNoExpired(true)
                             .accountNoLocked(true)
                             .credentialNoExpired(true)
                             .build();
        UserEntity userCreated = userRepository.save(newUser);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_".concat(RolesEnum.DEFAULT_USER.name())));
        userCreated.getRoles().stream().flatMap(role -> role.getPermissions().stream()).forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(), userCreated.getPassword(),authorities);
        String token = jwtUtils.createToken(authentication);

        //Sending email..
        final String welcomeMailMessage = "Welcome to our store...";
        final String welcomeMailSubject = "Thanks for choosing us, you have created an account.";

        emailService.sendEmail(userCreated.getEmail(),welcomeMailMessage,welcomeMailSubject);

        return new AuthResponseDTO(userCreated.getUsername(), "User created Successfully", token, true);
    }

    @Override
    public AuthResponseDTO login(AuthLoginDTO user) throws UsernameNotFoundException,BadCredentialsException{
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.username());
        if(userDetails == null){
            throw new BadCredentialsException("Invalid username or password.");
        }

        if(!passwordEncoder.matches(user.password(), userDetails.getPassword())){
            throw new BadCredentialsException("Invalid password.");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.username(),userDetails.getPassword(),userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtils.createToken(authentication);
        return new AuthResponseDTO(user.username(), "User logged sucessfully.",token , true);
    }

    @Override
    @Transactional
    public ProductCartEntity addProductToCart(Long productId, int quantity) throws ProductNotFoundException, PremiumProductException {
        UserEntity user = userRepository.findUserEntityByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).orElseThrow(()-> new UsernameNotFoundException("User could not be found"));
        ProductEntity product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        if(product.isPremium() && !(user.getRoles().contains(roleRepository.findRoleEntityByRolesEnum(RolesEnum.PREMIUM_USER)))){
            throw new PremiumProductException();
        }

        ProductCartEntity productCart = ProductCartEntity.builder()
                                        .product(product)
                                        .quantity(quantity).build();

        user.getCart().add(productCart);
        userRepository.save(user);
        return productCart;
    }

    @Override
    @Transactional
    public ProductCartEntity editCartProduct(Long productCartId, int quantity) throws ProductNotFoundException{
        ProductCartEntity productCart = productCartRepository.findById(productCartId).orElseThrow(()->new ProductNotFoundException());
        productCart.setQuantity(quantity);
        return productCartRepository.save(productCart);
    }

    @Override
    @Transactional
    public ProductCartEntity deleteCartProduct(Long productCartId) throws UsernameNotFoundException,ProductNotFoundException {
        UserEntity user = userRepository.findUserEntityByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
        ProductCartEntity product = productCartRepository.findById(productCartId).orElseThrow(()-> new ProductNotFoundException());

        user.getCart().remove(product);
        userRepository.save(user);
        
        return product;
    }

    @Override
    @Transactional
    public void cleanUpCartItems() {
        UserEntity user = userRepository.findUserEntityByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
        user.getCart().removeAll(user.getCart());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public InvoiceDTO purchaseCart(Long addressId) throws AddressNotFoundException,InsufficientMoneyException{
        UserEntity user = userRepository.findUserEntityByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).orElseThrow(() -> new UsernameNotFoundException("User could not be found"));

        AddressEntity address = addressRepository.findById(addressId).orElseThrow(AddressNotFoundException::new);

        if(!user.getAddresses().contains(address)){
            throw new AddressNotFoundException();
        }

        double totalCost = this.validatePurchase(user);

        //If user is premium there is a discount
        if(user.getRoles().contains(roleRepository.findRoleEntityByRolesEnum(RolesEnum.PREMIUM_USER))){
            totalCost -= totalCost*0.1;
        }

        totalCost += totalCost*InvoiceEntity.taxes;

        InvoiceEntity invoice = InvoiceEntity.builder()
                .user(user)
                .address(address)
                .totalCost(totalCost)
                .cart(user.getCart().stream().map(ProductCartEntity::clone).toList())
                .build();

        //We remove products from its stock

        user.getCart().forEach(product -> product.getProduct().setStock(product.getProduct().getStock()-(product.getQuantity())));


        deleteCartProductsFromRepository(user.getCart());

        user.getCart().clear();

        user.setWallet(user.getWallet()-totalCost);

        user.getInvoices().add(invoice);

        userRepository.save(user);

        //Sending email..
        final String purchaseMessage = "Purchase done correctly";
        final String purchaseMailSubject = "Thanks for choosing us, you have done a purchase.";

        emailService.sendEmail(user.getEmail(),purchaseMessage,purchaseMailSubject);

        return new InvoiceDTO(user.getUsername(), user.getEmail(), user.getPhoneNumber(),address, invoice.getCart(), (InvoiceEntity.taxes*100) +"%" , totalCost);
    }

    @Transactional
    public void deleteCartProductsFromRepository(List<ProductCartEntity> cart){
        productCartRepository.deleteAllById(cart.stream().map(ProductCartEntity::getId).toList());
    }

    private double validatePurchase(UserEntity user) throws InsufficientMoneyException{
        double totalCost = user.getCart().stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();

        if(totalCost > user.getWallet()){
            throw new InsufficientMoneyException();
        }

        return totalCost;
    }

    @Override
    @Transactional
    public Set<AddressEntity> getAddresses() {
        UserEntity user = userRepository.findUserEntityByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
        return user.getAddresses();
    }

    @Override
    @Transactional
    public AddressEntity addAddress(AddressEntity address) {
        UserEntity user = userRepository.findUserEntityByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
        addressRepository.save(address);
        user.getAddresses().add(address);
        userRepository.save(user);
        return address;
    }

    @Override
    @Transactional
    public AddressEntity deleteAddress(Long addressId) throws AddressNotFoundException {
        UserEntity user = userRepository.findUserEntityByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
        AddressEntity address = addressRepository.findById(addressId).orElseThrow(AddressNotFoundException::new);

        user.getAddresses().remove(address);

        return address;
    }

    @Override
    @Transactional
    public AddressEntity editAddress(AddressEntity newAddress, Long addressId) throws AddressNotFoundException {
        UserEntity user = userRepository.findUserEntityByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
        boolean addressFound = false;

        for(AddressEntity address: user.getAddresses()){
            if(address.getId().equals(addressId)){
                address.setCountry(newAddress.getCountry());
                address.setAddress(newAddress.getAddress());
                address.setCity(newAddress.getCity());
                address.setZipCode(newAddress.getZipCode());
                addressFound = true;
                break;
            }
        }

        if(!addressFound){
            throw new AddressNotFoundException();
        }

        userRepository.save(user);
        return newAddress;
    }

    @Override
    public List<ProductCartEntity> getCartProducts() {
        UserEntity user = userRepository.findUserEntityByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
        return user.getCart();
    }

}
