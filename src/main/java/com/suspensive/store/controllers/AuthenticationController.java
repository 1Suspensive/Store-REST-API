package com.suspensive.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suspensive.store.models.dto.AuthLoginDTO;
import com.suspensive.store.models.dto.AuthResponseDTO;
import com.suspensive.store.models.dto.AuthSignUpUserDTO;
import com.suspensive.store.services.IUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private IUserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponseDTO> signup(@RequestBody @Valid AuthSignUpUserDTO user) throws Exception{
        return new ResponseEntity<>(userService.createUser(user),HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid AuthLoginDTO user){
        return new ResponseEntity<>(userService.login(user),HttpStatus.OK);
    }
}
