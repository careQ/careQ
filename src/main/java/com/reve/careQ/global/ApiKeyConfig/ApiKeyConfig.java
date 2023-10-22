package com.reve.careQ.global.ApiKeyConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiKeyConfig {

    @Value("${api.mapKey}")
    private String mapKey;

    @Value("${api.pharmacyApiKey}")
    private String pharmacyApiKey;

    @Value("${api.hospitalApiKey}")
    private String hospitalApiKey;

    @Bean
    public ApiKeys apiKeys() {
        return new ApiKeys(mapKey, pharmacyApiKey, hospitalApiKey);
    }

}
