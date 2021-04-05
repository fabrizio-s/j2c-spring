package com.j2c.j2c.service.util;

import lombok.NonNull;

import javax.validation.ConstraintViolation;
import java.util.Locale;

public final class ExceptionMessageUtils {

    private static final String[] suffixes = new String[]{ "Entity", "BO" };

    private ExceptionMessageUtils() {}

    public static String entityTypeName(@NonNull final Class<?> type) {
        final String typeName = replace(type.getSimpleName());
        return typeName.replaceAll("(.)([A-Z])", "$1 $2").toLowerCase(Locale.ROOT);
    }

    public static String constraintViolationFormattedMessage(@NonNull final ConstraintViolation<?> constraintViolation) {
        final String[] properties = constraintViolation.getPropertyPath().toString().split("\\.");
        String path = properties[properties.length - 1];
        String message = constraintViolation.getMessage();
        if (path != null && path.matches("<[a-z]+ element>")) {
            final String collectionProperty = properties[properties.length - 2];
            final int index = collectionProperty.indexOf('[');
            path = collectionProperty.substring(0, index);
            if ("must not be null".equals(message)) {
                message = "must not contain null elements";
            } else {
                path = path + "' element";
            }
        }
        path = splitCamelCase(path);
        return String.format("%s %s", path, message);
    }

    private static final String splitCamelCase(final String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        ).toLowerCase(Locale.ENGLISH);
    }

    private static String replace(final String typeSimpleName) {
        for (final String suffix : suffixes) {
            if (typeSimpleName.endsWith(suffix)) {
                return typeSimpleName.replace(suffix, "");
            }
        }
        return typeSimpleName;
    }

}
