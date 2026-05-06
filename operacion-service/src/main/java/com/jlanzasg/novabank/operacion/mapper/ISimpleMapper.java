package com.jlanzasg.novabank.operacion.mapper;

import java.util.Set;

/**
 * The interface Simple mapper.
 *
 * @param <E> the type parameter
 * @param <O> the type parameter
 */
public interface ISimpleMapper<E, O> {

    /**
     * To simple response dto o.
     *
     * @param entity the entity
     * @return the o
     */
// Convierte un Entity a un DTO
    O toSimpleResponseDTO(E entity);

    /**
     * To simple response dto list set.
     *
     * @param entites the entites
     * @return the set
     */
// Convierte una lista de Entities a una lista de DTOs
    default Set<O> toSimpleResponseDTOList(Set<E> entites) {
        if (entites == null) return null;
        return entites.stream()
                .map(this::toSimpleResponseDTO)
                .collect(java.util.stream.Collectors.toSet());
    }
}
