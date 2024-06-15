package com.suspensive.store.controllers;

import com.suspensive.store.models.dto.BasicResponseDTO;
import com.suspensive.store.models.dto.InvoiceDTO;
import com.suspensive.store.models.entities.ProductEntity;
import com.suspensive.store.services.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@Tag(
        name = "Invoices",
        description = "Controller to handle invoices"
)
@RequestMapping("invoices")
public class InvoiceController {

    private final UserServiceImpl userService;

    public InvoiceController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get user invoices",
            description = "This method returns user invoices along some user details",
            tags = {"Invoices"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = InvoiceDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BasicResponseDTO.class)
                    )
                )
            }
    )
    @GetMapping
    public ResponseEntity<BasicResponseDTO<Set<InvoiceDTO>>> getInvoices(){
        Set<InvoiceDTO> invoices = userService.getInvoices();

        if(invoices.isEmpty()){
            return new ResponseEntity<>(new BasicResponseDTO<>("You dont have invoices",null),HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(new BasicResponseDTO<>("User invoices",invoices), HttpStatus.OK);
    }
}
