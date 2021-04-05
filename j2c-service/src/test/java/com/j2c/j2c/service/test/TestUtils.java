package com.j2c.j2c.service.test;

import lombok.NonNull;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Assertions;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.j2c.j2c.service.test.ReflectionUtils.getAllFieldValues;
import static org.mockito.Mockito.spy;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public final class TestUtils {

    private static final EasyRandom easyRandom;

    static {
//        final LocalTime currentTime = LocalTime.now();
//        final LocalDate currentDate = LocalDate.now();
        final EasyRandomParameters parameters = new EasyRandomParameters()
                .seed(randomLong())
                .collectionSizeRange(2, 5)
                .stringLengthRange(2, 12)
                .charset(StandardCharsets.UTF_8)
//                .timeRange(currentTime.minusHours(6), currentTime.plusHours(6))
//                .dateRange(currentDate.minusDays(3), currentDate.plusDays(3))
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

    public static boolean anyIsNull(@NonNull final Object... objects) {
        return Arrays.stream(objects).anyMatch(Objects::isNull);
    }

    public static long randomLong() {
        return ThreadLocalRandom.current().nextLong();
    }

    public static Object spyField(final Object obj, final String fieldName) {
        final Object fieldValue = Objects.requireNonNull(getField(obj, fieldName),
                "value of '" + fieldName + "' on object " + obj + " must not be null");
        final Object spiedFieldValue = spy(fieldValue);
        setField(obj, fieldName, spiedFieldValue);
        return spiedFieldValue;
    }

    public static <T> Stream<T> nullable(final Collection<T> collection) {
        return Optional.ofNullable(collection).stream()
                .flatMap(Collection::stream);
    }

    public static <T> List<T> transformFirst(final List<T> list, final Consumer<T> consumer) {
        consumer.accept(list.get(0));
        return list;
    }

    public static <T> List<T> removeFirst(final List<T> list) {
        list.remove(0);
        return list;
    }

    public static boolean nullOrEmpty(final Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

}
