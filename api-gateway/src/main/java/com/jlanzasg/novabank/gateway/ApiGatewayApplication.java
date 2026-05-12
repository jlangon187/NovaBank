package com.jlanzasg.novabank.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import reactor.core.publisher.Hooks;

/**
 * The type Api gateway application.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}