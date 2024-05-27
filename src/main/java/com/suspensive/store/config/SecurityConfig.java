package com.suspensive.store.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.suspensive.store.config.filter.JwtTokenValidatorFilter;
import com.suspensive.store.services.UserDetailsServiceImpl;
import com.suspensive.store.util.JwtUtils;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
        .csrf(crsf -> crsf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(http->{
            //Public Requests
            http.requestMatchers(HttpMethod.POST,"/auth/**").permitAll();
            http.requestMatchers(HttpMethod.GET,"/products").permitAll();
            http.requestMatchers(HttpMethod.GET,"/products/filter").permitAll();

            //Private Requests
            //Store Controller
            http.requestMatchers(HttpMethod.GET,"/cart").hasAnyAuthority("DEFAULT_PURCHASE","PREMIUM_PURCHASE");
            http.requestMatchers(HttpMethod.PATCH,"/cart/add/{productId}").hasAnyAuthority("DEFAULT_PURCHASE","PREMIUM_PURCHASE");
            http.requestMatchers(HttpMethod.PATCH,"/cart/edit/{productId}").hasAnyAuthority("DEFAULT_PURCHASE","PREMIUM_PURCHASE");
            http.requestMatchers(HttpMethod.PATCH,"/cart/delete/{productId}").hasAnyAuthority("DEFAULT_PURCHASE","PREMIUM_PURCHASE");
            http.requestMatchers(HttpMethod.PATCH,"/cart/clean-up").hasAnyAuthority("DEFAULT_PURCHASE","PREMIUM_PURCHASE");
            http.requestMatchers(HttpMethod.PATCH,"/cart/purchase").hasAnyAuthority("DEFAULT_PURCHASE","PREMIUM_PURCHASE");

            //Addresses Requests
            http.requestMatchers(HttpMethod.GET,"/addresses").hasAnyRole("DEFAULT_USER", "PREMIUM_USER");
            http.requestMatchers(HttpMethod.PATCH,"/addresses/add").hasAnyRole("DEFAULT_USER", "PREMIUM_USER");
            http.requestMatchers(HttpMethod.PATCH,"/addresses/delete/{addressId}").hasAnyRole("DEFAULT_USER", "PREMIUM_USER");
            http.requestMatchers(HttpMethod.PATCH,"/addresses/edit/{addressId}").hasAnyRole("DEFAULT_USER", "PREMIUM_USER");

            //Product Controller
            http.requestMatchers(HttpMethod.POST,"/products/add").hasAuthority("SELL");
            http.requestMatchers(HttpMethod.POST,"/products/add/productsList").hasAuthority("SELL");
            http.requestMatchers(HttpMethod.PATCH,"/products/edit/{productId}").hasAuthority("SELL");
            http.requestMatchers(HttpMethod.DELETE,"/products/delete/{productId}").hasRole("ADMIN");



            http.anyRequest().denyAll();
        })
        .addFilterBefore(new JwtTokenValidatorFilter(jwtUtils), BasicAuthenticationFilter.class)
        .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsService){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(this.passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



}
