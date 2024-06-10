package com.suspensive.store.config.filter;

import java.io.IOException;
import java.util.Collection;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.suspensive.store.util.JwtUtils;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenValidatorFilter extends OncePerRequestFilter{
    private final JwtUtils jwtUtils;

    public JwtTokenValidatorFilter(JwtUtils jwtUtils){
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
                String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

                if(jwtToken != null){
                    jwtToken = jwtToken.substring(7);
                    DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
                    String username = jwtUtils.getUsername(decodedJWT);
                    String stringAuthorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();
                    Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(stringAuthorities);
                    SecurityContext context = SecurityContextHolder.getContext();
                    Authentication authentication = new UsernamePasswordAuthenticationToken(username,null,authorities);
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                }
                filterChain.doFilter(request, response);
            }

}
