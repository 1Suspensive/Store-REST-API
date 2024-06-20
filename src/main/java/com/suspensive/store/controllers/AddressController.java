package com.suspensive.store.controllers;


import java.util.Set;

import com.suspensive.store.services.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.suspensive.store.models.dto.BasicResponseDTO;
import com.suspensive.store.models.entities.AddressEntity;
import com.suspensive.store.models.exceptions.AddressNotFoundException;


@RestController
@Tag(
        name = "Addresses",
        description = "Controller to handle addresses"
)
public class AddressController {

    private final UserServiceImpl userService;

    public AddressController(UserServiceImpl userService) {
        this.userService = userService;
    }


    @Operation(summary = "Get user addresses",
            description = "This method returns the user addresses",
            tags = {"Addresses"},
            responses = @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AddressEntity.class))
                    )
            )
    )
    @GetMapping("addresses")
    public Set<AddressEntity> getAddresses(){
        return userService.getAddresses();
    }

    @Operation(
            summary = "Add new address",
            description = "This method adds a new address",
            tags = {"Addresses"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Address body",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddressEntity.class),
                            examples = @ExampleObject(
                                    name = "Simple address body",
                                    summary = "This is a simple address",
                                    value = """
                                            {
                                                "country":"Colombia",
                                                "address":"Cra 33A #42-24",
                                                "city":"Medellin",
                                                "zipCode":"5555"
                                            }
                                        """
                            )
                    )
            ),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Address added successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AddressEntity.class))
                    )

            )

    )
    @PatchMapping("addresses/add")
    public ResponseEntity<BasicResponseDTO<Set<AddressEntity>>> addAddress(@RequestBody AddressEntity address){
        BasicResponseDTO<Set<AddressEntity>> response = new BasicResponseDTO<>("Address added successfully!", userService.addAddress(address));
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }


    @Operation(
            summary = "Delete an address",
            description = "This method deletes an address",
            tags = {"Addresses"},
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Address deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddressEntity.class)
                    )

            )

    )
    @PatchMapping("addresses/delete/{addressId}")
    public ResponseEntity<BasicResponseDTO<Set<AddressEntity>>> deleteAddress(@PathVariable Long addressId) throws AddressNotFoundException{
        BasicResponseDTO<Set<AddressEntity>> response = new BasicResponseDTO<>("Address deleted successfully!", userService.deleteAddress(addressId));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(
            summary = "Edit an address",
            description = "This method edits an address",
            tags = {"Addresses"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Address body",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddressEntity.class),
                            examples = @ExampleObject(
                                    name = "Simple address body",
                                    summary = "This is a simple address",
                                    value = """
                                            {
                                                "country":"Colombia",
                                                "address":"Cra 45 #42-24",
                                                "city":"Medellin",
                                                "zipCode":"5555"
                                            }
                                        """
                            )
                    )
            ),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Address edited successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AddressEntity.class))
                    )

            )

    )
    @PatchMapping("addresses/edit/{addressId}")
    public ResponseEntity<BasicResponseDTO<Set<AddressEntity>>> editAddress(@RequestBody AddressEntity address, @PathVariable Long addressId) throws AddressNotFoundException{
        BasicResponseDTO<Set<AddressEntity>> response = new BasicResponseDTO<>("Address edited successfully!", userService.editAddress(address, addressId));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
