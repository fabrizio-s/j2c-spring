package com.j2c.j2c.service.domain.product;

import com.j2c.j2c.domain.entity.ProductVariant;
import com.j2c.j2c.domain.entity.ProductVariantImage;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateProductVariantResult {

    @NonNull
    private final ProductVariant createdVariant;

    @NonNull
    private final List<ProductVariantImage> createdImages;

}
