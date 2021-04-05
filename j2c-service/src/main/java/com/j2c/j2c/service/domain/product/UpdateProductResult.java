package com.j2c.j2c.service.domain.product;

import com.j2c.j2c.domain.entity.Product;
import com.j2c.j2c.domain.entity.ProductTag;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdateProductResult {

    @NonNull
    private final Product updatedProduct;

    @NonNull
    private final List<ProductTag> addedTags;

    @NonNull
    private final List<ProductTag> removedTags;

}
