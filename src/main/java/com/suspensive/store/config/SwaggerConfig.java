package com.suspensive.store.config;

import org.springframework.http.HttpHeaders;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "Store API",
        description = "This is a rest api that simulates a store",
        version = "1.0.0",
        contact = @Contact(
            name = "Jeferson Ospina",
            email = "jeospinaga@unal.edu.co"
        )
    ),
    servers = {
        @Server(
        description = "DEV SERVER",
        url = "http://localhost:8080"
        ),
        @Server(
            description = "PROD SERVER",
            url = "NOT DEFINED"
        )
    },
    security = @SecurityRequirement(
        name = "Security Token"
    )
)

@SecurityScheme(
    name = "Security Token",
    description = "Access token",
    type = SecuritySchemeType.HTTP,
    paramName = HttpHeaders.AUTHORIZATION,
    in = SecuritySchemeIn.HEADER,
    scheme = "bearer",
    bearerFormat = "JWT"
)

public class SwaggerConfig {

}
