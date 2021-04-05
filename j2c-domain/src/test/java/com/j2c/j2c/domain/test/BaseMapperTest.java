package com.j2c.j2c.domain.test;

import com.google.common.collect.ImmutableList;
import org.springframework.lang.NonNull;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.j2c.j2c.domain.util.J2cUtils.optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class BaseMapperTest {

    private static final String MESSAGE = "field '%s' on object of type %s ";

    protected void assertMappings(final Object source, final Object target, final String... excludedFields) {
        assertNotNull(source, "source object must not be null");
        assertNotNull(target, "target object must not be null");
        getAllFieldNamesExcept(target, excludedFields)
                .forEach(f -> assertMapping(source, target, f));
    }

    protected List<String> getAllFieldNamesExcept(@NonNull final Object obj, final String... excludedFields) {
        final List<String> excludedFieldNames = distinct(excludedFields);
        excludedFieldNames.forEach(fieldName -> verifyFieldExists(obj, fieldName));
        return Arrays.stream(obj.getClass().getDeclaredFields())
                .map(Field::getName)
                .filter(f -> included(f, excludedFieldNames))
                .collect(ImmutableList.toImmutableList());
    }

    private static List<String> distinct(final String... strings) {
        return optional(strings)
                .map(Arrays::asList)
                .stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private static boolean included(final String fieldName, final List<String> excludedFieldNames) {
        return !excludedFieldNames.contains(fieldName);
    }

    private static void assertMapping(final Object source, final Object target, final String fieldName) {
        final Object sourceFieldValue = getFieldValue(source, fieldName);
        final Object targetFieldValue = getFieldValue(target, fieldName);
        assertEquals(sourceFieldValue, targetFieldValue, String.format("'%s' on target differs from source", fieldName));
    }

    private static Object getFieldValue(final Object obj, final String fieldName) {
        final Object fieldValue = ReflectionTestUtils.getField(obj, fieldName);
        assertNotNull(
                fieldValue,
                String.format(MESSAGE, fieldName, obj.getClass().getSimpleName()) + "must not be null"
        );
        return fieldValue;
    }

    private static void verifyFieldExists(final Object obj, final String fieldName) {
        try {
            obj.getClass().getDeclaredField(fieldName);
        } catch (final NoSuchFieldException exception) {
            throw new RuntimeException(
                    String.format(MESSAGE, fieldName, obj.getClass().getSimpleName()) + "does not exist",
                    exception
            );
        }
    }

}
