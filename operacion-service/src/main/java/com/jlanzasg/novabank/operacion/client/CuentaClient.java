package com.jlanzasg.novabank.operacion.client;

import com.jlanzasg.novabank.operacion.dto.cuenta.response.CuentaResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cuenta-service", path = "/cuentas")
public interface CuentaClient {

    @GetMapping("/iban/{iban}")
    CuentaResponseDTO getCuentaByIban(@PathVariable("iban") String iban);

    @PutMapping("/iban/{iban}/saldo")
    void actualizarSaldo(@PathVariable("iban") String iban, @RequestParam("nuevoSaldo") Double nuevoSaldo);
}