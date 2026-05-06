package com.jlanzasg.novabank.cuenta.client;

import com.jlanzasg.novabank.cuenta.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.cuenta.exception.ServiceException;
import org.springframework.stereotype.Component;

@Component
public class ClienteServiceFallback implements ClienteClient {

    @Override
    public ClienteResponseDTO getClienteById(Long id) {
        throw new ServiceException("El servicio de clientes no está disponible en este momento. No se puede consultar la información del cliente con ID " + id);
    }
}