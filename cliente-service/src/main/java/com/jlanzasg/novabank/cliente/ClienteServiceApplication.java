package com.jlanzasg.novabank.cliente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * The type Cliente service application.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ClienteServiceApplication {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {SpringApplication.run(ClienteServiceApplication.class, args);}
}
