package com.jlanzasg.novabank.operacion.client;

import com.jlanzasg.novabank.operacion.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.operacion.exception.ServiceException;
import org.springframework.stereotype.Component;

@Component
public class CuentaServiceFallback implements CuentaClient {

    @Override
    public CuentaResponseDTO getCuentaByIban(String iban) {
        throw new ServiceException("El servicio de cuentas no está disponible en este momento. No se puede consultar el saldo de la cuenta " + iban);
    }

    @Override
    public void actualizarSaldo(String iban, Double nuevoSaldo) {
        throw new ServiceException("El servicio de cuentas no está disponible en este momento. La operación ha sido abortada por seguridad.");
    }
}