package com.jlanzasg.novabank.operacion.client;

import com.jlanzasg.novabank.operacion.dto.cuenta.response.CuentaResponseDTO;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The interface Cuenta client.
 */
@FeignClient(name = "cuenta-service", path = "/cuentas", fallback = CuentaServiceFallback.class)
@Retry(name = "cuenta-service")
public interface CuentaClient {

    /**
     * Gets cuenta by iban.
     *
     * @param iban the iban
     * @return the cuenta by iban
     */
    @GetMapping("/iban/{iban}")
    CuentaResponseDTO getCuentaByIban(@PathVariable("iban") String iban);

    /**
     * Actualizar saldo.
     *
     * @param iban       the iban
     * @param nuevoSaldo the nuevo saldo
     */
    @PutMapping("/iban/{iban}/saldo")
    void actualizarSaldo(@PathVariable("iban") String iban, @RequestParam("nuevoSaldo") Double nuevoSaldo);
}