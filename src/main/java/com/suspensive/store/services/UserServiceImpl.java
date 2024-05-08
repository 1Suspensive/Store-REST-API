package com.suspensive.store.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


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
import com.suspensive.store.models.entities.ProductEntity;
import com.suspensive.store.models.entities.RoleEntity;
import com.suspensive.store.models.entities.RolesEnum;
import com.suspensive.store.models.entities.UserEntity;
import com.suspensive.store.models.exceptions.AddressNotFoundException;
import com.suspensive.store.models.exceptions.InsufficientMoneyException;
import com.suspensive.store.models.exceptions.PremiumProductException;
import com.suspensive.store.models.exceptions.ProductNotFoundException;
import com.suspensive.store.repositories.AddressRepository;
import com.suspensive.store.repositories.ProductRepository;
import com.suspensive.store.repositories.RoleRepository;
import com.suspensive.store.repositories.UserRepository;
import com.suspensive.store.util.JwtUtils;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements IUserService{

    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private IEmailService emailService;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
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
    public ProductEntity addProductToCart(Long productId) throws ProductNotFoundException, PremiumProductException {
        UserEntity user = userRepository.findUserEntityByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).orElseThrow(()-> new UsernameNotFoundException("User could not be found"));
        ProductEntity product = productRepository.findById(productId).orElseThrow(()-> new ProductNotFoundException());
        if(product.isPremium() && !(user.getRoles().contains(roleRepository.findRoleEntityByRolesEnum(RolesEnum.PREMIUM_USER)))){
            throw new PremiumProductException();
        }
        user.getCart().add(product);
        userRepository.save(user);
        return product;
    }

    @Override
    @Transactional
    public ProductEntity deleteCartProduct(Long productId) throws ProductNotFoundException {
        UserEntity user = userRepository.findUserEntityByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
        ProductEntity product = productRepository.findById(productId).orElseThrow(()-> new ProductNotFoundException());

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

        AddressEntity address = addressRepository.findById(addressId).orElseThrow(()-> new AddressNotFoundException());

        if(!user.getAddresses().contains(address)){
            throw new AddressNotFoundException();
        }

        double totalCost = this.validatePurchase(user);

        //If user is premium, then there is a discount
        if(user.getRoles().contains(roleRepository.findRoleEntityByRolesEnum(RolesEnum.PREMIUM_USER))){
            totalCost -= totalCost*0.1;
        }

        InvoiceEntity invoice = new InvoiceEntity();
        //We add taxes
        totalCost += totalCost*invoice.getTaxes();

        //We remove a product from its stack
        user.getCart().forEach(product-> product.setStock(product.getStock()-1));

        invoice.setAddress(address);
        invoice.setTotalCost(totalCost);
        invoice.setCart(user.getCart());

        user.getInvoices().add(invoice);

        userRepository.save(user);

        //Sending email..
        final String purchaseMessage = "Purchase done correctly";
        final String purchaseMailSubject = "Thanks for choosing us, you have done a purchase.";

        emailService.sendEmail(user.getEmail(),purchaseMessage,purchaseMailSubject);

        return new InvoiceDTO(user.getUsername(), user.getEmail(), user.getPhoneNumber(),address, invoice.getCart(), (invoice.getTaxes()*100) +"%" , totalCost);
    }

    public double validatePurchase(UserEntity user) throws InsufficientMoneyException{
        double totalCost = user.getCart().stream().mapToDouble(ProductEntity::getPrice).sum();

        if(totalCost > user.getWallet()){
            throw new InsufficientMoneyException();
        }

        return totalCost;
    }


    @Override
    public Set<AddressEntity> getAddresses() {
        UserEntity user = userRepository.findUserEntityByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
        return (Set<AddressEntity>) user.getAddresses();
    }

    @Override
    @Transactional
    public AddressEntity addAddress(AddressEntity address) {
        UserEntity user = userRepository.findUserEntityByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
        user.getAddresses().add(address);
        userRepository.save(user);
        return address;
    }

    @Override
    @Transactional
    public AddressEntity deleteAddress(Long addressId) throws AddressNotFoundException {
        UserEntity user = userRepository.findUserEntityByUsername(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).orElseThrow(() -> new UsernameNotFoundException("User could not be found"));
        AddressEntity address = addressRepository.findById(addressId).orElseThrow(()-> new AddressNotFoundException());

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

        if(addressFound == false){
            throw new AddressNotFoundException();
        }

        userRepository.save(user);
        return newAddress;
    }

}
