package com.j2c.j2c.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CardDTO implements PaymentMethodDTO {

    @ToString.Include
    @EqualsAndHashCode.Include
    @Schema(example = "PM123456")
    private final String id;

    @Schema(example = "visa")
    private final String brand;

    @Schema(example = "4242")
    private final String last4;

}
