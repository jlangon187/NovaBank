package com.jlanzasg.novabank.mapper.impl;

import com.jlanzasg.novabank.dto.cliente.request.ClienteRequestDTO;
import com.jlanzasg.novabank.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.mapper.IMapper;
import com.jlanzasg.novabank.model.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper implements IMapper<Cliente, ClienteRequestDTO, ClienteResponseDTO> {

    @Override
    public Cliente toEntity(ClienteRequestDTO dto) {
        if (dto == null) return null;
        return Cliente.builder()
                .dni(dto.getDni())
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
        dto.setDni(entity.getDni());
        dto.setNombre(entity.getNombre());
        dto.setApellidos(entity.getApellidos());
        dto.setEmail(entity.getEmail());
        dto.setTelefono(entity.getTelefono());
        dto.setFecha(entity.getFecha());

        return dto;
    }
}
