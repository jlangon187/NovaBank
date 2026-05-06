package com.jlanzasg.novabank.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * The type Auth service application.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuthServiceApplication {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {SpringApplication.run(AuthServiceApplication.class, args);}
}
