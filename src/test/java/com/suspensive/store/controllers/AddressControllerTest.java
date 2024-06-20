package com.suspensive.store.controllers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("stg")
public class AddressControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;


    @BeforeAll
    static void setup(UserDetailsService userDetailsService){
        UserDetails userDetails = userDetailsService.loadUserByUsername("Jeferson");
        Authentication authentication = new UsernamePasswordAuthenticationToken("Jeferson",userDetails.getPassword(),userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @Order(2)
    public void testGetAddresses(){}
}
