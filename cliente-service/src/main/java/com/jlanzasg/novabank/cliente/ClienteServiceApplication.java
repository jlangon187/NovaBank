package com.jlanzasg.novabank.cliente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

/**
 * The type Cliente service application.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableR2dbcAuditing
public class ClienteServiceApplication {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {SpringApplication.run(ClienteServiceApplication.class, args);}
}
