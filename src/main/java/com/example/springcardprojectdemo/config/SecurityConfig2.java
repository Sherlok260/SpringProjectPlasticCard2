package com.example.springcardprojectdemo.config;

import com.example.springcardprojectdemo.security.JwtFilter;
import com.example.springcardprojectdemo.security.JwtFilter2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig2 {
    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    JwtFilter2 jwtFilter2;
    @Autowired
    AuthenticationProvider authenticationProvider;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeRequests()
                .requestMatchers("/" ,"/api/login", "/api/register", "/api/f_password").permitAll()
                .requestMatchers("/api/hello2").permitAll()
                .anyRequest()
                .authenticated();
        http.addFilterBefore(jwtFilter2, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authenticationProvider(authenticationProvider);
        return http.build();
    }
}