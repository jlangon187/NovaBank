package com.jlanzasg.novabank.operacion.mapper.impl;

import com.jlanzasg.novabank.operacion.dto.cuenta.response.CuentaResponseDTO;
import com.jlanzasg.novabank.operacion.dto.cuenta.response.CuentaSaldoResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * The type Cuenta mapper.
 */
@Component
public class CuentaMapper {

    public CuentaResponseDTO toResponseDTO(CuentaResponseDTO cuentaDTO) {
        return cuentaDTO;
    }

    public Set<CuentaResponseDTO> toResponseDTOList(Set<CuentaResponseDTO> cuentaDTOs) {
        return cuentaDTOs.stream()
                .map(this::toResponseDTO)
                .collect(java.util.stream.Collectors.toSet());
    }

    public CuentaSaldoResponseDTO toSaldoResponseDTO(CuentaResponseDTO cuentaResponseDTO) {
        if (cuentaResponseDTO == null) return null;

        CuentaSaldoResponseDTO dto = new CuentaSaldoResponseDTO();
        dto.setBalance(cuentaResponseDTO.getBalance());
        return dto;
    }
}
