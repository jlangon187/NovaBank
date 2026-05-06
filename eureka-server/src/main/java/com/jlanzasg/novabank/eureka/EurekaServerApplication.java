package com.jlanzasg.novabank.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * The type Eureka server application.
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}