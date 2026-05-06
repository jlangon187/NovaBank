package com.jlanzasg.novabank.cuenta.client;

import com.jlanzasg.novabank.cuenta.dto.cliente.response.ClienteResponseDTO;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * The interface Cliente client.
 */
@FeignClient(name = "cliente-service", path = "/clientes", fallback = ClienteServiceFallback.class)
@Retry(name = "cliente-service")
public interface ClienteClient {

    /**
     * Gets cliente by id.
     *
     * @param id the id
     * @return the cliente by id
     */
    @GetMapping("/{id}")
    ClienteResponseDTO getClienteById(@PathVariable("id") Long id);
}