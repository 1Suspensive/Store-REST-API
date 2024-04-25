package com.suspensive.store.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.suspensive.store.models.entities.UserEntity;
import com.suspensive.store.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findUserEntityByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User does not exist"));
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRolesEnum().name()))));
        user.getRoles().stream().flatMap(role -> role.getPermissions().stream())
            .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));
        return new User(user.getUsername(),
        user.getPassword(),
        user.isEnabled(),
        user.isAccountNoExpired(),
        user.isCredentialNoExpired(),
        user.isAccountNoLocked(),authorities);
    }

}
