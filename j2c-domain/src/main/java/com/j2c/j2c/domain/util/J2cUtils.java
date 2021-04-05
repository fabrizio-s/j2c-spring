package com.j2c.j2c.domain.util;

import java.util.Collection;
import java.util.Optional;

public final class J2cUtils {

    private J2cUtils() {}

    public static <T> Optional<T> optional(final T obj) {
        return Optional.ofNullable(obj);
    }

    public static boolean containsNull(final Collection<?> collection) {
        try {
            return collection.contains(null);
        } catch (final NullPointerException ignored) {
            return false;
        }
    }

    public static <T> T assertNotNull(final T value, final String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
        return value;
    }

}
