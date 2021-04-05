package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.Entity;
import com.j2c.j2c.domain.exception.EntitiesDoNotExistException;
import com.j2c.j2c.domain.exception.EntityDoesNotExistException;
import com.j2c.j2c.domain.exception.NullIdentityException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;
import java.util.stream.Collectors;

import static com.j2c.j2c.domain.util.J2cUtils.containsNull;

abstract class BaseRepository<T extends Entity<ID>, ID> {

    protected final Class<T> type;
    private final JpaRepository<T, ID> repository;

    protected BaseRepository(
            final Class<T> type,
            final JpaRepository<T, ID> repository
    ) {
        this.type = type;
        this.repository = repository;
    }

    public T findById(final ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException(type, id));
    }

    public Optional<T> findByIdDoNotThrow(final ID id) {
        return repository.findById(id);
    }

    public long count() {
        return repository.count();
    }

    public void verifyExistsById(final ID id) {
        if (!existsById(id)) {
            throw new EntityDoesNotExistException(type, id);
        }
    }

    public T getReference(final ID id) {
        return repository.getOne(id);
    }

    public T save(final T entity) {
        return repository.save(entity);
    }

    public void removeById(final ID id) {
        repository.deleteById(id);
    }

    public void remove(final T entity) {
        repository.delete(entity);
    }

    public boolean existsById(final ID id) {
        return repository.existsById(id);
    }

    public void removeAll(final Collection<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        repository.deleteAll(entities);
    }

    public List<T> saveAll(final Collection<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return repository.saveAll(entities);
    }

    public Page<T> findAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<T> findAllById(final Set<ID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        } else if (containsNull(ids)) {
            throw new NullIdentityException(type);
        }
        final List<T> foundEntities = repository.findAllById(ids);
        final Set<Object> foundIds = foundEntities.stream()
                .map(Entity::getId)
                .collect(Collectors.toSet());
        final Set<Object> notFoundsIds = new HashSet<>(ids);
        notFoundsIds.removeAll(foundIds);
        if (!notFoundsIds.isEmpty()) {
            throw new EntitiesDoNotExistException(type, notFoundsIds);
        }
        return foundEntities;
    }

    public List<T> findAllByIdDoNotThrow(final Set<ID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        } else if (containsNull(ids)) {
            throw new NullIdentityException(type);
        }
        return repository.findAllById(ids);
    }

    protected boolean isNew(final T entity) {
        return entity.getId() == null;
    }

}
