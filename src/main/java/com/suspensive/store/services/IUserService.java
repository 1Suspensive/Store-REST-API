package com.suspensive.store.services;

import com.suspensive.store.models.dto.AuthResponseDTO;
import com.suspensive.store.models.dto.AuthSignUpUserDTO;

public interface IUserService {
    AuthResponseDTO createUser(AuthSignUpUserDTO user);
}
