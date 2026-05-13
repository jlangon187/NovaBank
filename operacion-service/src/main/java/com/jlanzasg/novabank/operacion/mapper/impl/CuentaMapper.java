package com.jlanzasg.novabank.operacion.mapper.impl;

import com.jlanzasg.novabank.operacion.dto.cuenta.CuentaResponseDTO;
import com.jlanzasg.novabank.operacion.dto.cuenta.CuentaSaldoResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * The type Cuenta mapper.
 */
@Component
public class CuentaMapper {

    /**
     * To response dto cuenta response dto.
     *
     * @param cuentaDTO the cuenta dto
     * @return the cuenta response dto
     */
    public CuentaResponseDTO toResponseDTO(CuentaResponseDTO cuentaDTO) {
        return cuentaDTO;
    }

    /**
     * To response dto list set.
     *
     * @param cuentaDTOs the cuenta dt os
     * @return the set
     */
    public Set<CuentaResponseDTO> toResponseDTOList(Set<CuentaResponseDTO> cuentaDTOs) {
        return cuentaDTOs.stream()
                .map(this::toResponseDTO)
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * To saldo response dto cuenta saldo response dto.
     *
     * @param cuentaResponseDTO the cuenta response dto
     * @return the cuenta saldo response dto
     */
    public CuentaSaldoResponseDTO toSaldoResponseDTO(CuentaResponseDTO cuentaResponseDTO) {
        if (cuentaResponseDTO == null) return null;

        CuentaSaldoResponseDTO dto = new CuentaSaldoResponseDTO();
        dto.setBalance(cuentaResponseDTO.getBalance());
        return dto;
    }
}
