package com.jlanzasg.novabank.cliente.mapper.impl;

import com.jlanzasg.novabank.cliente.dto.cliente.request.ClienteRequestDTO;
import com.jlanzasg.novabank.cliente.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.cliente.mapper.IMapper;
import com.jlanzasg.novabank.cliente.model.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper implements IMapper<Cliente, ClienteRequestDTO, ClienteResponseDTO> {

    @Override
    public Cliente toEntity(ClienteRequestDTO dto) {
        if (dto == null) return null;
        return Cliente.builder()
                .dni(dto.getDni().toUpperCase())
                .nombre(dto.getNombre())
                .apellidos(dto.getApellidos())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .build();
    }

    @Override
    public ClienteResponseDTO toResponseDTO(Cliente entity) {
        if (entity == null) return null;
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setId(entity.getId());
        dto.setDni(entity.getDni().toUpperCase());
        dto.setNombre(entity.getNombre());
        dto.setApellidos(entity.getApellidos());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setFecha(entity.getFecha());

        return dto;
    }
}
