package com.j2c.j2c.domain.test;

import lombok.NonNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.util.ReflectionTestUtils.getField;

public final class ReflectionUtils {

    private ReflectionUtils() {}

    public static List<Object> getAllFieldValues(@NonNull final Object obj) {
        return Arrays.stream(obj.getClass().getDeclaredFields())
                .map(f -> getField(obj, f.getName())).collect(Collectors.toList());
    }

    public static Field findField(final Object obj, final String fieldName) {
        return findField(obj.getClass(), fieldName);
    }

    public static Field findField(final Class<?> clazz, final String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (final NoSuchFieldException exception) {
            throw new RuntimeException(exception);
        }
    }

}
