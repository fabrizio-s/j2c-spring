package com.j2c.j2c.service.domain.product;

import lombok.*;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RemoveProductCategoryImagesEvent {

    @NonNull
    private final Long rootCategoryId;

    @NonNull
    private final Set<String> imageFilenames;

}
