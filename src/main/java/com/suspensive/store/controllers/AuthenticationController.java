package com.suspensive.store.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suspensive.store.models.dto.AuthLoginDTO;
import com.suspensive.store.models.dto.AuthResponseDTO;
import com.suspensive.store.models.dto.AuthSignUpUserDTO;
import com.suspensive.store.services.interfaces.IUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(
    name = "Authentication",
    description = "Controller for authentication"
)
public class AuthenticationController {
    
    private final IUserService userService;

    public AuthenticationController(IUserService userService) {
        this.userService = userService;
    }

    @Operation(
        summary = "Sign up user",
        description = "This method register an user and returns the authentication token along with user details",
        tags = {"Authentication"},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description="Authentication request with username, password, email, phone number and wallet",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthSignUpUserDTO.class),
                examples = @ExampleObject(
                    name = "Simple Sign Up",
                    summary = "This is a simple example of the method sign up",
                    value = "{ \"username\": \"Susensive\", \"password\": \"1234\", \"email\": \"suspensive@gmail.com\", \"phoneNumber\": \"3005918272\", \"wallet\": 200}"
                )
            )
        ),
        responses = @ApiResponse(
            responseCode = "201",
            description = "Successful authentication",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponseDTO.class)
            )
        )
    )
    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponseDTO> signup(@RequestBody @Valid AuthSignUpUserDTO user) throws Exception{
        return new ResponseEntity<>(userService.createUser(user),HttpStatus.CREATED);
    }


    @Operation(
        summary = "Login user",
        description = "This method authenticates an user and returns the authentication token along with user details",
        tags = {"Authentication"},
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description="Authentication request with username and password",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthLoginDTO.class),
                examples = @ExampleObject(
                        name = "Simple admin log-in",
                        summary = "This is a simple log-in",
                        value = "{ \"username\": \"Jeferson\", \"password\": \"1234\"}"
                )
            )
        ),
        responses = @ApiResponse(
            responseCode = "200",
            description = "Successful authentication",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponseDTO.class)
            )
        )
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid AuthLoginDTO user){
        return new ResponseEntity<>(userService.login(user),HttpStatus.OK);
    }
}
