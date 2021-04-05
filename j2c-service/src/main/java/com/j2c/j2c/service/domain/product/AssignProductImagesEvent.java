package com.j2c.j2c.service.domain.product;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AssignProductImagesEvent {

    @NonNull
    private final Set<UUID> uploadedImageIds;

    @NonNull
    private final Long productId;

}
