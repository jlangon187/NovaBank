package com.jlanzasg.novabank.cuenta.mapper;

import java.util.Set;

/**
 * The interface Mapper.
 *
 * @param <E> the type parameter
 * @param <I> the type parameter
 * @param <O> the type parameter
 */
public interface IMapper<E, I, O> {

    /**
     * To entity e.
     *
     * @param dto the dto
     * @return the e
     */
// Convierte un DTO a un Entity
    E toEntity(I dto);

    /**
     * To response dto o.
     *
     * @param entity the entity
     * @return the o
     */
// Convierte un Entity a un DTO
    O toResponseDTO(E entity);

    /**
     * To response dto list set.
     *
     * @param entites the entites
     * @return the set
     */
// Convierte una lista de Entities a una lista de DTOs
    default Set<O> toResponseDTOList(Set<E> entites) {
        if (entites == null) return null;
        return entites.stream()
                .map(this::toResponseDTO)
                .collect(java.util.stream.Collectors.toSet());
    }
}
