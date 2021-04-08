package com.j2c.j2c.service.mapper;

import com.google.common.collect.ImmutableList;
import com.j2c.j2c.service.dto.DTOBuilder;
import org.mapstruct.ObjectFactory;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

abstract class BaseDTOMapper<D, B extends DTOBuilder<D>, E> {

    @ObjectFactory
    protected abstract B builder();

    public abstract B fromEntity(E entity);

    public List<D> fromEntities(final Collection<E> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .filter(Objects::nonNull)
                .map(this::fromEntity)
                .map(DTOBuilder::build)
                .collect(ImmutableList.toImmutableList());
    }

    public Page<D> fromEntities(final Page<E> entities) {
        if (entities == null || entities.isEmpty()) {
            return Page.empty();
        }
        return entities.map(entity -> fromEntity(entity).build());
    }

}
