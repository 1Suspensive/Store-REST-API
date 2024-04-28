package com.suspensive.store.models.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginDTO(@NotBlank String username,
                           @NotBlank String password) {

}
