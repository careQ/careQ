package com.reve.careQ.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    @Order(0)
    SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admins/**")
                .formLogin(
                        formLogin -> formLogin
                                .loginPage("/admins/login")
                                .defaultSuccessUrl("/admins")

                )
                .logout(
                        logout -> logout
                                .logoutUrl("/admins/logout")
                                .logoutSuccessUrl("/")
                );

        return http.build();
    }

    @Bean
    @Order(1)
    SecurityFilterChain memberSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(
                        formLogin -> formLogin
                                .loginPage("/members/login")
                                .defaultSuccessUrl("/members")

                )
                .oauth2Login(
                        oauth2Login -> oauth2Login
                                .loginPage("/members/login")
                                .defaultSuccessUrl("/members")
                )
                .logout(
                        logout -> logout
                                .logoutUrl("/members/logout")
                                .logoutSuccessUrl("/")
                );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}