package com.j2c.j2c.service.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Line {

    @NotNull
    @Schema(example = "37")
    private final Long id;

    @NotNull
    @Positive
    @Schema(example = "1")
    private final Integer quantity;

}
