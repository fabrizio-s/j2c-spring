package com.j2c.j2c.service.test;

import lombok.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    public static <T> T newInstance(final Class<T> clazz) {
        return newInstance(getConstructor(clazz));
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

    public static List<Field> getFieldsOfType(@NonNull final Class<?> target, @NonNull final Class<?> searchType) {
        final Field[] fields  = target.getDeclaredFields();
        return Arrays.stream(fields)
                .filter(f -> searchType.isAssignableFrom(f.getType()))
                .collect(Collectors.toList());
    }

    private static <T> T newInstance(final Constructor<T> cons, final Object... args) {
        try {
            return cons.newInstance(args);
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static <T> Constructor<? extends T> getConstructor(final Class<T> clazz, final Class<?>... argTypes) {
        try {
            final Constructor<? extends T> constructor = clazz.getDeclaredConstructor(argTypes);
            constructor.setAccessible(true);
            return constructor;
        } catch (final NoSuchMethodException exception) {
            throw new RuntimeException(exception);
        }
    }

}
