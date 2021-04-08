package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.exception.DomainException;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

import static com.j2c.j2c.domain.entity.MaxLengths.IMAGEFILENAME_MAXLENGTH;
import static com.j2c.j2c.domain.exception.DomainErrorMessages.IMAGE_DOES_NOT_BELONG_TO_VARIANT;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@javax.persistence.Entity
@Table(name = "productvariantimage",
        indexes = @Index(columnList = "variant_id")
)
public class ProductVariantImage extends BaseEntity<Long> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @Column(name = "filename", nullable = false, updatable = false,
            length = IMAGEFILENAME_MAXLENGTH)
    private String filename;

    @Getter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private ProductVariant variant;

    @SuppressWarnings("unused")
    ProductVariantImage() {}

    @Builder
    private ProductVariantImage(
            final String filename,
            final ProductVariant variant
    ) {
        this.filename = assertNotNull(filename, "filename");
        this.variant = assertNotNull(variant, "variant");
    }

    public ProductVariantImage verifyBelongsToVariant(final ProductVariant variant) {
        if (!belongsToVariant(variant)) {
            throw new DomainException(String.format(IMAGE_DOES_NOT_BELONG_TO_VARIANT, id, variant.getId()), this);
        }
        return this;
    }

    public boolean belongsToVariant(final ProductVariant variant) {
        if (variant == null) {
            return false;
        }
        return variant.getId() != null && variant.getId().equals(this.variant.getId());
    }

}
