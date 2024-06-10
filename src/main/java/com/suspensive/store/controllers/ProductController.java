package com.suspensive.store.controllers;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.converters.ResponseSupportConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.suspensive.store.models.dto.BasicResponseDTO;
import com.suspensive.store.models.entities.ProductEntity;
import com.suspensive.store.models.exceptions.ProductNotFoundException;
import com.suspensive.store.services.IProductService;

@RestController
@RequestMapping("/products")
@Tag(
       name = "Products",
       description = "Controller to handle products"
)
public class ProductController {
    @Autowired
    private IProductService productService;

    @Operation(summary = "Get store products",
            description = "This method returns the store products along their details",
            tags = {"Products"},
            responses = @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductEntity.class))
                    )
            )
    )
    @GetMapping
    public List<ProductEntity> getProducts(){
        return productService.getProducts();
    }

    @Operation(
            summary = "Get products by category",
            description = "This methods returns products with a given category",
            tags = {"Products"},
            responses = @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductEntity.class))
                    )
            )
    )
    @GetMapping("/filter")
    public List<ProductEntity> getProductsByCategory(@RequestParam String category){
        return productService.getProducts(category);
    }

    @Operation(
            summary = "Add new product",
            description = "This method adds a new Product",
            tags = {"Products"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product body",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductEntity.class),
                            examples = @ExampleObject(
                                name = "Simple product body",
                                summary = "This is a simple product",
                                value = """
                                        {
                                            "name": "TV",
                                            "price": 200.5,
                                            "category": "Electronics",
                                            "stock": 100,
                                            "premium": false
                                        }
                                        """
                            )
                    )
            ),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Product added successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductEntity.class)
                    )

            )

    )
    @PostMapping("/add")
    public ResponseEntity<BasicResponseDTO<ProductEntity>> addProduct(@RequestBody ProductEntity product){
        BasicResponseDTO<ProductEntity> response = new BasicResponseDTO<>("Product added successfully.", productService.addProduct(product));
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @Operation(
            summary = "Add products list",
            description = "This method adds a list of new products",
            tags = {"Products"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A list of products",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductEntity.class)),
                            examples = @ExampleObject(
                                    name = "Simple list of products",
                                    summary = "This is a simple list of products",
                                    value = """
                                        [
                                        {
                                            "name": "TV",
                                            "price": 200.5,
                                            "category": "Electronics",
                                            "stock": 100,
                                            "premium": false
                                        },
                                        {
                                            "name": "Table",
                                            "price": 100,
                                            "category": "Home",
                                            "stock": 10,
                                            "premium": true
                                        }
                                        ]
                                        """
                            )

                    )
            ),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Products added successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductEntity.class))
                    )
            )

    )
    @PostMapping("/add/productsList")
    public ResponseEntity<BasicResponseDTO<List<ProductEntity>>> addProducts(@RequestBody List<ProductEntity> products){
        BasicResponseDTO<List<ProductEntity>> response = new BasicResponseDTO<>("Products added successfully.", productService.addProducts(products));
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @Operation(
            summary = "Edit a product",
            description = "This method edits a product with a given ID",
            tags = {"Products"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product body",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductEntity.class),
                            examples = @ExampleObject(
                                    name = "Simple product body",
                                    summary = "This is a simple product",
                                    value = """
                                        {
                                            "name": "TV",
                                            "price": 200.5,
                                            "category": "Electronics",
                                            "stock": 100,
                                            "premium": false
                                        }
                                        """
                            )
                    )
            ),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Product edited successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductEntity.class)
                    )

            )

    )
    @PatchMapping("/edit/{productId}")
    public ResponseEntity<BasicResponseDTO<ProductEntity>> editProduct(@PathVariable Long productId, @RequestBody ProductEntity productModified) throws ProductNotFoundException{
        BasicResponseDTO<ProductEntity> response = new BasicResponseDTO<>("Product edited successfully.", productService.editProduct(productId, productModified));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a product",
            description = "This method deletes a product with a given ID",
            tags = {"Products"},
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "Product deleted successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BasicResponseDTO.class)
                    )
            )

    )
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<BasicResponseDTO> deleteProduct(@PathVariable Long productId) throws ProductNotFoundException{
        productService.deleteProduct(productId);
        BasicResponseDTO response = new BasicResponseDTO("Product deleted successfully.", null);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
