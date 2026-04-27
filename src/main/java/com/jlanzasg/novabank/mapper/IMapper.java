package com.jlanzasg.novabank.mapper;

import java.util.List;

public interface IMapper<E, I, O> {

    // Convierte un DTO a un Entity
    E toEntity(I dto);

    // Convierte un Entity a un DTO
    O toResponseDTO(E entity);

    // Convierte una lista de Entities a una lista de DTOs
    default List<O> toResponseDTOList(List<E> entites) {
        if (entites == null) return null;
        return entites.stream()
                .map(this::toResponseDTO)
                .toList();
    }
}
