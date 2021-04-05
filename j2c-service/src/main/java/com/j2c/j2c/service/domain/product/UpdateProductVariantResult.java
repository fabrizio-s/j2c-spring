package com.j2c.j2c.service.domain.product;

import com.j2c.j2c.domain.entity.ProductVariant;
import com.j2c.j2c.domain.entity.ProductVariantImage;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdateProductVariantResult {

    @NonNull
    private final ProductVariant updatedVariant;

    @NonNull
    private final List<ProductVariantImage> addedImages;

}
