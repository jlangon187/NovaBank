package com.jlanzasg.novabank.cuenta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * The type Cuenta service application.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class CuentaServiceApplication {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CuentaServiceApplication.class, args);}
}
