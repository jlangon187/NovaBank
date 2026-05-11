package com.jlanzasg.novabank.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * The type Exchange service application.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ExchangeServiceApplication {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {SpringApplication.run(ExchangeServiceApplication.class, args);}
}
