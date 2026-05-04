package com.jlanzasg.novabank.operacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * The type Operacion service application.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class OperacionServiceApplication {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {SpringApplication.run(OperacionServiceApplication.class, args);}
}
