package com.j2c.j2c.domain.test;

import lombok.NonNull;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Assertions;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.mockito.Mockito.spy;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public final class TestUtils {

    private static final EasyRandom easyRandom;

    static {
        final EasyRandomParameters parameters = new EasyRandomParameters()
                .seed(randomLong())
                .collectionSizeRange(2, 5)
                .stringLengthRange(2, 12)
                .charset(StandardCharsets.UTF_8)
                .randomizationDepth(3)
                .scanClasspathForConcreteTypes(true)
                .objectPoolSize(20)
                .bypassSetters(true);
        easyRandom = new EasyRandom(parameters);
    }

    private TestUtils() {}

    public static <T> T nextObject(@NonNull final Class<T> clazz) {
        final T t = easyRandom.nextObject(clazz);
        getAllFieldValues(t).forEach(Assertions::assertNotNull);
        return t;
    }

    public static long randomLong() {
        return randomLong(0L, Long.MAX_VALUE);
    }

    public static long randomLong(final long min, final long max) {
        return ThreadLocalRandom.current().nextLong();
    }

    public static Object spyField(final Object obj, final String fieldName) {
        final Object fieldValue = Objects.requireNonNull(getField(obj, fieldName),
                "value of '" + fieldName + "' on object " + obj + " must not be null");
        final Object spiedFieldValue = spy(fieldValue);
        setField(obj, fieldName, spiedFieldValue);
        return spiedFieldValue;
    }

    private static List<Object> getAllFieldValues(@NonNull final Object obj) {
        return Arrays.stream(obj.getClass().getDeclaredFields())
                .map(f -> getField(obj, f.getName())).collect(Collectors.toList());
    }

}
