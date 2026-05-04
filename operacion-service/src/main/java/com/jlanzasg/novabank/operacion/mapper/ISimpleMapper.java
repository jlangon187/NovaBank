package com.jlanzasg.novabank.operacion.mapper;

import java.util.Set;

public interface ISimpleMapper<E, O> {

    // Convierte un Entity a un DTO
    O toSimpleResponseDTO(E entity);

    // Convierte una lista de Entities a una lista de DTOs
    default Set<O> toSimpleResponseDTOList(Set<E> entites) {
        if (entites == null) return null;
        return entites.stream()
                .map(this::toSimpleResponseDTO)
                .collect(java.util.stream.Collectors.toSet());
    }
}
