package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.exception.DomainException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.j2c.j2c.domain.entity.MaxLengths.*;
import static com.j2c.j2c.domain.exception.DomainErrorMessages.*;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;
import static java.time.LocalDateTime.now;

@javax.persistence.Entity
@Table(name = "product")
public class Product extends BaseEntity<Long> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @Column(name = "name", nullable = false,
            length = PRODUCT_NAME_MAXLENGTH)
    private String name;

    @Getter
    @Setter
    @Column(name = "image_filename", length = IMAGEFILENAME_MAXLENGTH)
    private String imageFilename;

    @Getter
    @Setter
    @Column(name = "description", length = PRODUCT_DESCRIPTION_MAXLENGTH)
    private String description;

    @Getter
    @Column(name = "digital", nullable = false, updatable = false)
    private boolean digital;

    @Getter
    @Column(name = "published", nullable = false)
    private boolean published;

    @Getter
    @Column(name = "last_published")
    private LocalDateTime lastPublished;

    @Getter
    @Column(name = "last_unpublished")
    private LocalDateTime lastUnpublished;

    @Getter
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "default_variant_id",
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private ProductVariant defaultVariant;

    @Getter
    @Column(name = "default_price", nullable = false,
            precision = 8, scale = 2)
    private Long defaultPrice;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private ProductCategory category;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,
            mappedBy = "product", orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "product", orphanRemoval = true)
    private Set<ProductToTagAssociation> tagAssociations;

    private static final long defaultNumberOfMinutesToWaitBeforeRePublish = 15L;

    @SuppressWarnings("unused")
    Product() {}

    @Builder
    private Product(
            final String name,
            final String imageFilename,
            final String description,
            final boolean digital,
            final Long defaultPrice,
            final ProductCategory category
    ) {
        this.name = assertNotNull(name, "name");
        this.imageFilename = imageFilename;
        this.description = description;
        this.digital = digital;
        this.defaultPrice = assertNotNull(defaultPrice, "defaultPrice");
        this.category = category;
    }

    @Builder(builderClassName = "ProductVariantCreator",
            builderMethodName = "newVariant",
            buildMethodName = "add")
    private ProductVariant addNewVariant(
            final String defaultVariantName,
            final String name,
            final Integer mass,
            final Long price
    ) {
        setCurrentDefaultVariantName(defaultVariantName);
        final ProductVariant variant = ProductVariant.builder()
                .name(name)
                .mass(mass)
                .price(price)
                .product(this)
                .build();
        variants.add(variant);
        return variant;
    }

    public void removeVariant(final ProductVariant variant) {
        if (variant == null || !variant.belongsToProduct(this)) {
            return;
        } else if (variant.isDefaultVariant()) {
            throw new DomainException(String.format(CANNOT_REMOVE_DEFAULT_VARIANT, variant.getId()), this);
        }
        variants.remove(variant);
    }

    public void publish() {
        if (published) {
            throw new DomainException(String.format(ALREADY_PUBLISHED, id), this);
        } else if (!hasDefaultVariant()) {
            throw new DomainException(String.format(CANNOT_PUBLISH_WITHOUT_DEFAULT_VARIANT, id), this);
        } else if (!xMinutesHavePassedSinceLastUnpublish()) {
            throw new DomainException(String.format(CANNOT_PUBLISH_BEFORE_X_MINUTES, id, minutesToWaitBeforeRePublish()), this);
        }
        this.published = true;
        lastPublished = now();
    }

    public void unpublish() {
        if (!published) {
            throw new DomainException(String.format(ALREADY_UNPUBLISHED, id), this);
        }
        this.published = false;
        lastUnpublished = now();
    }

    public void setDefaultVariant(final ProductVariant defaultVariant) {
        if (defaultVariant == null) {
            throw new IllegalArgumentException("defaultVariant must not be null");
        } else if (!canSetDefaultVariant()) {
            throw new DomainException(String.format(CANNOT_SET_DEFAULT_VARIANT_IF_CURRENT_HAS_NO_NAME, id), this);
        } else if (!defaultVariant.belongsToProduct(this)) {
            throw new DomainException(String.format(CANNOT_SET_UNOWNED_DEFAULT_VARIANT, id), this);
        }
        this.defaultVariant = defaultVariant;
    }

    public void setName(final String name) {
        this.name = assertNotNull(name, "name");
    }

    public void setDefaultPrice(final Long defaultPrice) {
        this.defaultPrice = assertNotNull(defaultPrice, "defaultPrice");
    }

    private void setCurrentDefaultVariantName(final String newDefaultVariantName) {
        if (defaultVariant == null) {
            return;
        }
        if (defaultVariant.getName() == null && newDefaultVariantName == null) {
            throw new DomainException(String.format(DEFAULT_VARIANT_NAME_REQUIRED, id), this);
        } else if (newDefaultVariantName != null) {
            defaultVariant.setName(newDefaultVariantName);
        }
    }

    private boolean xMinutesHavePassedSinceLastUnpublish() {
        if (lastUnpublished == null) {
            return true;
        }
        final LocalDateTime publishableTime = lastUnpublished.plusMinutes(minutesToWaitBeforeRePublish());
        return now().isAfter(publishableTime);
    }

    private boolean canSetDefaultVariant() {
        if (defaultVariant == null) {
            return true;
        }
        return defaultVariant.getName() != null;
    }

    private long minutesToWaitBeforeRePublish() {
        return defaultNumberOfMinutesToWaitBeforeRePublish;
    }

    private boolean hasDefaultVariant() {
        return defaultVariant != null;
    }

    @PreRemove
    protected void remove() {
        defaultVariant = null;
    }

}
