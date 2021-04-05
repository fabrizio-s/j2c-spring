package com.j2c.j2c.domain.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

public class ProductToTagAssociationPK implements Serializable {

    @Getter
    private Long product;

    @Getter
    private Long tag;

    @SuppressWarnings("unused")
    ProductToTagAssociationPK() {}

    @Builder
    private ProductToTagAssociationPK(
            @NonNull final Long product,
            @NonNull final Long tag
    ) {
        this.product = assertNotNull(product, "product");
        this.tag = assertNotNull(tag, "tag");
    }

    @Override
    public String toString() {
        return "ProductToTagAssociationPK(" +
                "product=" + product +
                ", tag=" + tag +
                ')';
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, tag);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ProductToTagAssociationPK that = (ProductToTagAssociationPK) o;
        return Objects.equals(product, that.product) && Objects.equals(tag, that.tag);
    }

}
