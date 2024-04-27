package com.suspensive.store.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthSignUpUserDTO(@NotBlank String username,
                                @NotBlank String password,
                                @NotBlank String email,
                                @NotBlank String phoneNumber,
                                @NotNull Double wallet
) {

}
