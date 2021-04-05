package com.j2c.j2c.service.domain.product;

import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AssignProductCategoryImageEvent {

    @NonNull
    private final UUID uploadedImageId;

    @NonNull
    private final Long rootCategoryId;

}
