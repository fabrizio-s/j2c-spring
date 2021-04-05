package com.j2c.j2c.service.util;

import com.j2c.j2c.service.input.Line;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class MergedLines {

    private final Map<Long, Integer> mergedLines;

    private static final MergedLines EMPTY = new MergedLines(Collections.emptyList());

    private MergedLines(@NonNull final List<Line> lines) {
        if (lines.isEmpty()) {
            mergedLines = Collections.emptyMap();
        } else {
            this.mergedLines = mergeLines(lines);
        }
    }

    public Set<Long> getIds() {
        return mergedLines.keySet();
    }

    public int getQuantity(final Long id) {
        final Integer quantity = mergedLines.get(id);
        return Optional.ofNullable(quantity).orElse(0);
    }

    private Map<Long, Integer> mergeLines(final List<Line> lines) {
        return lines.stream()
                .collect(
                        Collectors.toMap(
                                Line::getId,
                                Line::getQuantity,
                                Integer::sum
                        )
                );
    }

    public static MergedLines merge(final List<Line> lines) {
        if (lines == null || lines.isEmpty()) {
            return EMPTY;
        }
        return new MergedLines(lines);
    }

}
