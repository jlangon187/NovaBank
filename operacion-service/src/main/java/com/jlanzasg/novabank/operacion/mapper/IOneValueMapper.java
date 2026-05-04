package com.jlanzasg.novabank.operacion.mapper;

import java.util.Set;

public interface IOneValueMapper<E, O> {

    // Convierte un Entity a un DTO
    O toOneValueResponseDTO(E entity);

    // Convierte una lista de Entities a una lista de DTOs
    default Set<O> toOneValueResponseDTOList(Set<E> entites) {
        if (entites == null) return null;
        return entites.stream()
                .map(this::toOneValueResponseDTO)
                .collect(java.util.stream.Collectors.toSet());
    }
}
