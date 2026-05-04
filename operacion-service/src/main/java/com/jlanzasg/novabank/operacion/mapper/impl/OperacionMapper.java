package com.jlanzasg.novabank.operacion.mapper.impl;

import com.jlanzasg.novabank.operacion.dto.operacion.request.OperacionRequestDTO;
import com.jlanzasg.novabank.operacion.dto.operacion.response.MovimientoResponseDTO;
import com.jlanzasg.novabank.operacion.mapper.IMapper;
import com.jlanzasg.novabank.operacion.model.Movimiento;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class OperacionMapper implements IMapper<Movimiento, OperacionRequestDTO, MovimientoResponseDTO> {

    @Override
    public Movimiento toEntity(OperacionRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Movimiento movimiento = new Movimiento();
        movimiento.setCantidad(dto.getImporte());
        return movimiento;
    }

    @Override
    public MovimientoResponseDTO toResponseDTO(Movimiento entity) {
        if (entity == null) {
            return null;
        }
        MovimientoResponseDTO responseDTO = new MovimientoResponseDTO();
        responseDTO.setIdMovimiento(entity.getId());
        responseDTO.setTipoMovimiento(entity.getTipo().name());
        responseDTO.setCantidad(entity.getCantidad());
        responseDTO.setFecha(entity.getFecha());
        return responseDTO;
    }

    @Override
    public Set<MovimientoResponseDTO> toResponseDTOList(Set<Movimiento> entites) {
        return IMapper.super.toResponseDTOList(entites);
    }
}