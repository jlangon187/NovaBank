package com.jlanzasg.novabank.cuenta.mapper;

import java.util.Set;

/**
 * The interface One value mapper.
 *
 * @param <E> the type parameter
 * @param <O> the type parameter
 */
public interface IOneValueMapper<E, O> {

    /**
     * To one value response dto o.
     *
     * @param entity the entity
     * @return the o
     */
// Convierte un Entity a un DTO
    O toOneValueResponseDTO(E entity);

    /**
     * To one value response dto list set.
     *
     * @param entites the entites
     * @return the set
     */
// Convierte una lista de Entities a una lista de DTOs
    default Set<O> toOneValueResponseDTOList(Set<E> entites) {
        if (entites == null) return null;
        return entites.stream()
                .map(this::toOneValueResponseDTO)
                .collect(java.util.stream.Collectors.toSet());
    }
}
