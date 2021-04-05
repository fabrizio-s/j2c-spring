package com.j2c.j2c.domain.test;

import com.google.common.collect.ImmutableList;
import com.j2c.j2c.domain.entity.Entity;
import com.j2c.j2c.domain.entity.ProductToTagAssociation;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.StreamSupport;

import static com.j2c.j2c.domain.test.ReflectionUtils.findField;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

//@SuppressWarnings({"SameParameterValue", "unused"})
public final class MockIdGenerator {

    private static final AtomicLong counter = new AtomicLong(1);
    private static final Set<Long> assignedLongIds = new HashSet<>();
    private static final Set<UUID> assignedUUIDs = new HashSet<>();
    private static final Set<String> assignedStringIds = new HashSet<>();

    private MockIdGenerator() {}

    @SuppressWarnings("unchecked")
    public static <T extends Entity<?>> void mockAdd(@NonNull final Collection<T> entities) {
        doAnswer(i -> {
            mockSaveEntity((T) i.getArguments()[0]);
            return i.callRealMethod();
        }).when(entities).add(isNotNull());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity<?>> void mockRepositorySave(@NonNull final JpaRepository<T, ?> repository) {
        when(repository.save(isNotNull()))
                .thenAnswer(i -> mockSaveEntity((T) i.getArguments()[0]));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity<?>> void mockRepositorySaveAll(@NonNull final JpaRepository<T, ?> repository) {
        when(repository.saveAll(anyIterable()))
                .thenAnswer(i -> mockSaveAllEntities((Iterable<T>) i.getArguments()[0]));
    }

    public static <T extends Entity<?>> void setNewId(@NonNull final T entity) {
        final String idFieldName = "id";
        final Object id = newId(entity, idFieldName);
        setField(entity, idFieldName, id);
    }

    public static <T extends Entity<?>> void setId(@NonNull final T entity, final Object id) {
        if (id == null) {
            setNewId(entity);
        } else {
            addId(id);
            setField(entity, "id", id);
        }
    }

    private static Long newLongId() {
        while (true) {
            final Long id = counter.getAndIncrement();
            if (!assignedLongIds.contains(id)) {
                assignedLongIds.add(id);
                return id;
            }
        }
    }

    private static void addId(@NonNull final Object id) {
        final Class<?> type = id.getClass();
        if (Long.class.equals(type)) {
            assignedLongIds.add((Long) id);
        } else if (UUID.class.equals(type)) {
            assignedUUIDs.add((UUID) id);
        } else if (String.class.equals(type)) {
            assignedStringIds.add((String) id);
        }  else {
            throw new RuntimeException("Unsupported type of id!!!1");
        }
    }

    private static UUID newUUID() {
        while (true) {
            final UUID uuid = UUID.randomUUID();
            if (!assignedUUIDs.contains(uuid)) {
                assignedUUIDs.add(uuid);
                return uuid;
            }
        }
    }

    private static String newStringId() {
        while (true) {
            final UUID uuid = UUID.randomUUID();
            final String id = uuid.toString();
            if (!assignedStringIds.contains(id)) {
                assignedStringIds.add(id);
                assignedUUIDs.add(uuid);
                return id;
            }
        }
    }

    private static <T extends Entity<?>> List<T> mockSaveAllEntities(final Iterable<T> entities) {
        return mockSaveAllEntities(entities, "id");
    }

    private static <T extends Entity<?>> List<T> mockSaveAllEntities(final Iterable<T> entities, final String idFieldName) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return StreamSupport.stream(entities.spliterator(), false)
                .map(e -> mockSaveEntity(e, idFieldName))
                .collect(ImmutableList.toImmutableList());
    }

    private static <T extends Entity<?>> T mockSaveEntity(final T entity) {
        return mockSaveEntity(entity, "id");
    }

    private static <T extends Entity<?>> T mockSaveEntity(final T entity, final String idFieldName) {
        if (entity == null) {
            return null;
        } else if (!isJPAEntityWithCompositeKey(entity)) {
            final Object fieldValue = entity.getId();
            if (fieldValue == null) {
                final Object newId = newId(entity, idFieldName);
                setField(entity, idFieldName, newId);
            }
        }
        return entity;
    }

    private static <T extends Entity<?>> Object newId(final T entity, final String idFieldName) {
        final Class<?> type = getIdType(entity, idFieldName);
        if (Long.class.equals(type)) {
            return newLongId();
        } else if (UUID.class.equals(type)) {
            return newUUID();
        } else if (String.class.equals(type)) {
            return newStringId();
        }
        throw new RuntimeException("Unsupported type of id!!!1");
    }

    private static <T extends Entity<?>> Class<?> getIdType(final T entity, final String idFieldName) {
        final Field field = findField(entity.getClass(), idFieldName);
        return field.getType();
    }

    private static boolean isJPAEntityWithCompositeKey(final Object entity) {
        final Class<?> type = entity.getClass();
        return ProductToTagAssociation.class.equals(type);
    }

}
