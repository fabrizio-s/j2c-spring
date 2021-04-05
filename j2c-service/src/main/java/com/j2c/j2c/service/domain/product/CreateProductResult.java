package com.j2c.j2c.service.domain.product;

import com.j2c.j2c.domain.entity.Product;
import com.j2c.j2c.domain.entity.ProductTag;
import com.j2c.j2c.domain.entity.ProductVariant;
import lombok.*;

import java.util.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateProductResult {

    @NonNull
    private final Product createdProduct;

    @NonNull
    private final ProductVariant createdVariant;

    @NonNull
    private final List<ProductTag> addedTags;

}
