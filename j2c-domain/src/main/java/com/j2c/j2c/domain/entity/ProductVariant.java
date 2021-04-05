package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.exception.DomainException;
import lombok.*;

import javax.persistence.*;
import java.util.List;

import static com.j2c.j2c.domain.entity.MaxLengths.PRODUCTVARIANT_NAME_MAXLENGTH;
import static com.j2c.j2c.domain.exception.DomainErrorMessages.*;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@javax.persistence.Entity
@Table(name = "productvariant",
        indexes = @Index(columnList = "product_id")
)
public class ProductVariant extends BaseEntity<Long> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @Column(name = "name",
            length = PRODUCTVARIANT_NAME_MAXLENGTH)
    private String name;

    @Getter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Product product;

    @Getter
    @Column(name = "mass",
            precision = 4, scale = 1)
    private Integer mass;

    @Getter
    @Setter
    @Column(name = "price",
            precision = 10, scale = 2)
    private Long price;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "variant", orphanRemoval = true)
    private List<ProductVariantImage> images;

    @SuppressWarnings("unused")
    ProductVariant() {}

    @Builder(access = AccessLevel.PACKAGE)
    private ProductVariant(
            final String name,
            final Product product,
            final Integer mass,
            final Long price
    ) {
        this.product = assertNotNull(product, "product");
        setMass(mass);
        this.name = name;
        this.price = price;
    }

    public void setName(final String name) {
        this.name = assertNotNull(name, "name");
    }

    public void setMass(final Integer mass) {
        if (product.isDigital()) {
            return;
        } else if (mass == null) {
            throw new DomainException(String.format(VARIANT_REQUIRES_MASS, product.getId()), this);
        }
        this.mass = mass;
    }

    public ProductVariant verifyBelongsToProduct(final Product product) {
        if (!belongsToProduct(product)) {
            throw new DomainException(String.format(VARIANT_DOES_NOT_BELONG_TO_PRODUCT, id, product.getId()), this);
        }
        return this;
    }

    public boolean belongsToProduct(final Product product) {
        if (product == null) {
            return false;
        }
        return product.isSameAs(this.product);
    }

    public boolean isDefaultVariant() {
        return isSameAs(product.getDefaultVariant());
    }

    public Long getEffectivePrice() {
        if (price != null) {
            return price;
        }
        return product.getDefaultPrice();
    }

}
