package com.j2c.j2c.service.domain.product;

import lombok.*;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RemoveProductImagesEvent {

    @NonNull
    private final Long productId;

    @NonNull
    private final Set<String> imageFilenames;

}
