package com.jlanzasg.novabank.mapper.impl;

import com.jlanzasg.novabank.dto.cuenta.request.CuentaRequestDTO;
import com.jlanzasg.novabank.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.mapper.IMapper;
import com.jlanzasg.novabank.model.Cuenta;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The type Cuenta mapper.
 */
@Component
public class CuentaMapper implements IMapper<Cuenta, CuentaRequestDTO, CuentaResponseDTO> {
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
    public List<CuentaResponseDTO> toResponseDTOList(List<Cuenta> entites) {
        return IMapper.super.toResponseDTOList(entites);
    }
}
