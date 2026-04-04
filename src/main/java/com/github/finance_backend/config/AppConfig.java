package com.github.finance_backend.config;

import com.github.finance_backend.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class AppConfig {
    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/test").permitAll()

                        // User management — Admin only
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")

                        // Records — write operations Admin only
                        .requestMatchers(HttpMethod.POST, "/api/v1/records").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/records/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/records/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/records/all").hasRole("ADMIN")

                        // Records — read operations for Analyst and Admin
                        .requestMatchers(HttpMethod.GET, "/api/v1/records/**").hasAnyRole("ANALYST", "ADMIN")

                        // Dashboard — all authenticated users
                        .requestMatchers("/api/v1/dashboard/**").authenticated()

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
