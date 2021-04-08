package com.j2c.j2c.domain.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@javax.persistence.Entity
@Table(name = "product_producttag")
@IdClass(ProductToTagAssociationPK.class)
public class ProductToTagAssociation extends BaseEntity<ProductToTagAssociationPK> {

    @Transient
    private ProductToTagAssociationPK id;

    @Id
    @Getter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Product product;

    @Id
    @Getter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "producttag_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private ProductTag tag;

    @SuppressWarnings("unused")
    ProductToTagAssociation() {}

    @Builder
    private ProductToTagAssociation(
            final Product product,
            final ProductTag tag
    ) {
        this.product = assertNotNull(product, "product");
        this.tag = assertNotNull(tag, "tag");
    }

    @Override
    public ProductToTagAssociationPK getId() {
        if (id == null) {
            id = ProductToTagAssociationPK.builder()
                    .product(product.getId())
                    .tag(tag.getId())
                    .build();
        }
        return id;
    }

    @Override
    public String toString() {
        final Long tagId = getTagId();
        final Long productId = getProductId();
        return "ProductToTagAssociation(product=" + productId + ", tag=" + tagId + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductId(), getTagId());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ProductToTagAssociation that = (ProductToTagAssociation) o;
        return Objects.equals(getProductId(), that.getProductId())
                && Objects.equals(getTagId(), that.getTagId());
    }

    private Long getProductId() {
        if (product == null) {
            return null;
        }
        return product.getId();
    }

    private Long getTagId() {
        if (tag == null) {
            return null;
        }
        return tag.getId();
    }

}
