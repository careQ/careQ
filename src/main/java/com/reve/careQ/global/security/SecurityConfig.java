package com.reve.careQ.global.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Qualifier("CustomFailureHandler")
    private final AuthenticationFailureHandler customFailureHandler;

    @Qualifier("AdminFailureHandler")
    private final AuthenticationFailureHandler adminFailureHandler;

    public SecurityConfig(@Qualifier("CustomFailureHandler") AuthenticationFailureHandler customFailureHandler,
                          @Qualifier("AdminFailureHandler") AuthenticationFailureHandler adminFailureHandler) {
        this.customFailureHandler = customFailureHandler;
        this.adminFailureHandler = adminFailureHandler;
    }

    @Bean
    @Order(0)
    SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admins/**")
                .formLogin(
                        formLogin -> formLogin
                                .loginPage("/admins/login")
                                .failureHandler(adminFailureHandler) // 로그인 실패 핸들러
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
                                .failureHandler(customFailureHandler) // 로그인 실패 핸들러
                                .defaultSuccessUrl("/members")

                )
                .oauth2Login(
                        oauth2Login -> oauth2Login
                                .loginPage("/members/login")
                                .failureHandler(customFailureHandler) // 로그인 실패 핸들러
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

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {

        ProviderManager authenticationManager = (ProviderManager)authenticationConfiguration.getAuthenticationManager();

        return authenticationManager;
    }

}