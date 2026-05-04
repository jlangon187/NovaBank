package com.jlanzasg.novabank.cuenta.mapper.impl;

import com.jlanzasg.novabank.cuenta.dto.cliente.response.ClienteResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.request.CuentaRequestDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaSaldoResponseDTO;
import com.jlanzasg.novabank.cuenta.dto.cuenta.response.CuentaSimpleResponseDTO;
import com.jlanzasg.novabank.cuenta.mapper.IMapper;
import com.jlanzasg.novabank.cuenta.mapper.IOneValueMapper;
import com.jlanzasg.novabank.cuenta.mapper.ISimpleMapper;
import com.jlanzasg.novabank.cuenta.model.Cuenta;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * The type Cuenta mapper.
 */
@Component
public class CuentaMapper implements IMapper<Cuenta, CuentaRequestDTO, CuentaResponseDTO>,
        ISimpleMapper<Cuenta, CuentaSimpleResponseDTO>, IOneValueMapper<Cuenta, CuentaSaldoResponseDTO> {

    @Override
    public Cuenta toEntity(CuentaRequestDTO dto) {
        if (dto == null) return null;
        return Cuenta.builder().build();
    }

    @Override
    public CuentaResponseDTO toResponseDTO(Cuenta entity) {
        if (entity == null) return null;

        CuentaResponseDTO dto = new CuentaResponseDTO();
        dto.setId(entity.getId());
        dto.setIban(entity.getIban());
        dto.setBalance(entity.getBalance());
        dto.setFecha(entity.getFecha());
        dto.setClienteId(entity.getClienteId());

        dto.setClienteName("Datos no solicitados");

        return dto;
    }

    public CuentaResponseDTO toResponseDTO(Cuenta entity, ClienteResponseDTO clienteDTO) {
        if (entity == null) return null;

        CuentaResponseDTO dto = this.toResponseDTO(entity);

        if (clienteDTO != null) {
            dto.setClienteName(clienteDTO.getNombre() + " " + clienteDTO.getApellidos() );
        } else {
            dto.setClienteName("Nombre del cliente no disponible");
        }
        return dto;
    }

    @Override
    public Set<CuentaResponseDTO> toResponseDTOList(Set<Cuenta> entites) {
        return entites.stream()
                .map(this::toResponseDTO)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public CuentaSimpleResponseDTO toSimpleResponseDTO(Cuenta entity) {
        if (entity == null) return null;

        CuentaSimpleResponseDTO dto = new CuentaSimpleResponseDTO();
        dto.setId(entity.getId());
        dto.setIban(entity.getIban());
        dto.setBalance(entity.getBalance());

        return dto;
    }

    @Override
    public Set<CuentaSimpleResponseDTO> toSimpleResponseDTOList(Set<Cuenta> entites) {
        return ISimpleMapper.super.toSimpleResponseDTOList(entites);
    }

    @Override
    public CuentaSaldoResponseDTO toOneValueResponseDTO(Cuenta entity) {
        if (entity == null) return null;

        CuentaSaldoResponseDTO dto = new CuentaSaldoResponseDTO();
        dto.setBalance(entity.getBalance());

        return dto;
    }

    @Override
    public Set<CuentaSaldoResponseDTO> toOneValueResponseDTOList(Set<Cuenta> entites) {
        return IOneValueMapper.super.toOneValueResponseDTOList(entites);
    }
}
