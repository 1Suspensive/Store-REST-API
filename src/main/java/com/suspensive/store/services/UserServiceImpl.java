package com.suspensive.store.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.suspensive.store.models.dto.AuthResponseDTO;
import com.suspensive.store.models.dto.AuthSignUpUserDTO;
import com.suspensive.store.models.entities.RoleEntity;
import com.suspensive.store.models.entities.RolesEnum;
import com.suspensive.store.models.entities.UserEntity;
import com.suspensive.store.repositories.RoleRepository;
import com.suspensive.store.repositories.UserRepository;
import com.suspensive.store.util.JwtUtils;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IEmailService emailService;

    @Override
    @Transactional
    public AuthResponseDTO createUser(AuthSignUpUserDTO user) {
        Set<RoleEntity> defaultRole = Set.of(roleRepository.findRoleEntityByRolesEnum(RolesEnum.DEFAULT_USER));
        UserEntity newUser = UserEntity.builder()
                             .username(user.username())
                             .password(passwordEncoder.encode(user.password()))
                             .email(user.email())
                             .phoneNumber(user.phoneNumber())
                             .wallet(user.wallet())
                             .roles(defaultRole)
                             .isEnabled(true)
                             .accountNoExpired(true)
                             .accountNoLocked(true)
                             .credentialNoExpired(true)
                             .build();
        UserEntity userCreated = userRepository.save(newUser);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_".concat(RolesEnum.DEFAULT_USER.name())));
        userCreated.getRoles().stream().flatMap(role -> role.getPermissions().stream()).forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(), userCreated.getPassword(),authorities);
        String token = jwtUtils.createToken(authentication);

        //Sending email..
        final String welcomeMailMessage = "Welcome to our store...";
        final String welcomeMailSubject = "Thanks for choosing us, you have created an account.";

        emailService.sendEmail(userCreated.getEmail(),welcomeMailMessage,welcomeMailSubject);

        return new AuthResponseDTO(userCreated.getUsername(), "User created Successfully", token, true);
    }

}
