package com.suspensive.store.models.dto;

public record BasicResponseDTO<T>(String message,
                               T object) {

}
