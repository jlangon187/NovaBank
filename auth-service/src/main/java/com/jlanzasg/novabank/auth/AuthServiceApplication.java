package com.jlanzasg.novabank.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * The type Auth service application.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableR2dbcRepositories
public class AuthServiceApplication {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {SpringApplication.run(AuthServiceApplication.class, args);}
}
