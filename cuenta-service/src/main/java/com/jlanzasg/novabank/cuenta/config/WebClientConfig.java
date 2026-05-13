package com.jlanzasg.novabank.cuenta.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The type Web client config.
 */
@Configuration
public class WebClientConfig {

    /**
     * Web client builder web client . builder.
     *
     * @return the web client . builder
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}