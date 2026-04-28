package com.jlanzasg.novabank.mapper.impl;

import com.jlanzasg.novabank.dto.cuenta.request.CuentaRequestDTO;
import com.jlanzasg.novabank.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.dto.cuenta.response.CuentaSaldoResponseDTO;
import com.jlanzasg.novabank.dto.cuenta.response.CuentaSimpleResponseDTO;
import com.jlanzasg.novabank.mapper.IMapper;
import com.jlanzasg.novabank.mapper.IOneValueMapper;
import com.jlanzasg.novabank.mapper.ISimpleMapper;
import com.jlanzasg.novabank.model.Cuenta;
import org.springframework.stereotype.Component;

import java.util.List;
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

        if (entity.getCliente() != null) {
            dto.setClienteId(entity.getCliente().getId());
            dto.setClienteName(entity.getCliente().getNombre() + " " + entity.getCliente().getApellidos());
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
