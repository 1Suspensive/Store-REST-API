package com.suspensive.store.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.suspensive.store.models.dto.BasicResponseDTO;
import com.suspensive.store.models.exceptions.AddressNotFoundException;
import com.suspensive.store.models.exceptions.InsufficientMoneyException;
import com.suspensive.store.models.exceptions.ProductNotFoundException;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler({ProductNotFoundException.class})
    public ResponseEntity<BasicResponseDTO> productNotFound(){
        BasicResponseDTO response = new BasicResponseDTO("Product could not be found",null);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({AddressNotFoundException.class})
    public ResponseEntity<BasicResponseDTO> addressNotFound(){
        BasicResponseDTO response = new BasicResponseDTO("Address could not be found", null);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({InsufficientMoneyException.class})
    public ResponseEntity<BasicResponseDTO> insufficientMoney(){
        BasicResponseDTO response = new BasicResponseDTO("You don't have enough money", null);
        return ResponseEntity.badRequest().body(response);
    }
}
