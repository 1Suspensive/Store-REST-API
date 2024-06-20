package com.suspensive.store.controllers;

import com.suspensive.store.models.dto.BasicResponseDTO;
import com.suspensive.store.models.dto.InvoiceDTO;
import com.suspensive.store.models.entities.ProductCartEntity;
import com.suspensive.store.models.exceptions.AddressNotFoundException;
import com.suspensive.store.models.exceptions.InsufficientMoneyException;
import com.suspensive.store.models.exceptions.PremiumProductException;
import com.suspensive.store.models.exceptions.ProductNotFoundException;
import com.suspensive.store.services.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(
        name = "Cart",
        description = "Controller to handle cart"
)
@RequestMapping("/cart")
public class CartController {

    private final UserServiceImpl userService;

    public CartController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get user cart",
            description = "This method returns the user cart along its products",
            tags = {"Cart"},
            responses = @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductCartEntity.class))
                    )
            )
    )
    @GetMapping
    public List<ProductCartEntity> getCartProducts(){
        return userService.getCartProducts();
    }

    @Operation(
            summary = "Add product to cart",
            description = "This method adds a product with his quantity to user cart",
            tags = {"Cart"},
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Product added to cart.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductCartEntity.class)
                    )

            )

    )
    @PatchMapping("add/{productId}")
    public ResponseEntity<BasicResponseDTO<ProductCartEntity>> addProductToCart(@PathVariable Long productId, @RequestParam int quantity) throws ProductNotFoundException, PremiumProductException {
        BasicResponseDTO<ProductCartEntity> response = new BasicResponseDTO<>("Product added to cart.", userService.addProductToCart(productId,quantity));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Operation(
            summary = "Edit product on cart",
            description = "This method edits the quantity of a product on cart",
            tags = {"Cart"},
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Product edited successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductCartEntity.class)
                    )

            )

    )
    @PatchMapping("edit/{productId}")
    public ResponseEntity<BasicResponseDTO<ProductCartEntity>> editProductCart(@PathVariable Long productId, @RequestParam int quantity) throws ProductNotFoundException{
        BasicResponseDTO<ProductCartEntity> response = new BasicResponseDTO<>("Product edited successfully.", userService.editCartProduct(productId,quantity));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(
            summary = "Delete product on cart",
            description = "This method deletes a product on cart",
            tags = {"Cart"},
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Product deleted from cart.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductCartEntity.class))
                    )

            )

    )
    @PatchMapping("delete/{productId}")
    public ResponseEntity<BasicResponseDTO<List<ProductCartEntity>>> deleteCartProduct(@PathVariable Long productId) throws ProductNotFoundException{
        BasicResponseDTO<List<ProductCartEntity>> response = new BasicResponseDTO<>("Product deleted from cart.", userService.deleteCartProduct(productId));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @Operation(
            summary = "Clean up products on cart",
            description = "This method cleans the user cart",
            tags = {"Cart"},
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Now your cart is empty!",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BasicResponseDTO.class)
                    )

            )

    )
    @PatchMapping("clean-up")
    public ResponseEntity<BasicResponseDTO<List<ProductCartEntity>>> cleanUpCartItems(){
        return new ResponseEntity<>(new BasicResponseDTO("Now your cart is empty!", userService.cleanUpCartItems()), HttpStatus.OK);
    }

    @Operation(
            summary = "Purchase cart",
            description = "This method purchases the user cart",
            tags = {"Cart"},
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Cart purchased correctly",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InvoiceDTO.class)
                    )

            )

    )
    @PatchMapping("purchase")
    public ResponseEntity<BasicResponseDTO<InvoiceDTO>> purchaseCart(@RequestParam Long addressId) throws AddressNotFoundException, InsufficientMoneyException {
        BasicResponseDTO<InvoiceDTO> response = new BasicResponseDTO<>("Cart purchased correctly", userService.purchaseCart(addressId));
        userService.cleanUpCartItems();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
