package com.suspensive.store.services;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmailServiceTest {

    private EmailServiceImpl emailService;

    @BeforeEach
    void setup(){
        this.emailService = new EmailServiceImpl();
    }

    @Test
    void testValidateEmail(){
        String email = "jefersonospina@gmail.com";
        try{
            emailService.validateEmail(email);
        }catch(Exception e){
            fail("No exception should be thrown for a valid email");
        }
    }

    @Test
     void testValidateInvalidEmail(){
        String email = "jeferson.com";

        assertThrows(Exception.class, () -> emailService.validateEmail(email));
    }

}
