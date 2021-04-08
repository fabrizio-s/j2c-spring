package com.j2c.j2c.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

import static com.j2c.j2c.domain.entity.MaxLengths.*;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@javax.persistence.Entity
@Table(name = "productcategory")
public class ProductCategory extends BaseEntity<Long> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @Column(name = "name", nullable = false,
            length = PRODUCTCATEGORY_NAME_MAXLENGTH)
    private String name;

    @Getter
    @Setter
    @Column(name = "description", length = PRODUCTCATEGORY_DESCRIPTION_MAXLENGTH)
    private String description;

    @Getter
    @Column(name = "image_filename", length = IMAGEFILENAME_MAXLENGTH)
    private String imageFilename;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_id", updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private ProductCategory root;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private ProductCategory parent;

    @Getter
    @Column(name = "lft", nullable = false)
    private int left = 1;

    @Getter
    @Column(name = "rgt", nullable = false)
    private int right = 2;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "parent", orphanRemoval = true)
    private List<ProductCategory> subCategories;

    @SuppressWarnings("unused")
    ProductCategory() {}

    @Builder
    private ProductCategory(
            final String name,
            final String description,
            final String imageFilename
    ) {
        this.name = assertNotNull(name, "name");
        this.description = description;
        this.imageFilename = imageFilename;
    }

    @Builder(builderClassName = "SubCategoryCreator",
            builderMethodName = "newSubCategory",
            buildMethodName = "add")
    private ProductCategory addNewSubCategory(
            final String name,
            final String description,
            final String imageFilename
    ) {
        final ProductCategory subCategory = builder()
                .name(name)
                .description(description)
                .imageFilename(imageFilename)
                .build();
        subCategory.root = getRootForNewSubCategory();
        subCategory.parent = this;
        subCategory.left = right;
        subCategory.right = right + 1;
        right += 2;
        return subCategory;
    }

    public void setImageFilename(final String imageFilename) {
        this.imageFilename = imageFilename;
    }

    public boolean isRootCategory() {
        return root == null;
    }

    public Long getRootCategoryId() {
        if (isRootCategory()) {
            return id;
        }
        return root.getId();
    }

    public void setName(final String name) {
        this.name = assertNotNull(name, "name");
    }

    private ProductCategory getRootForNewSubCategory() {
        if (isRootCategory()) {
            return this;
        }
        return root;
    }

}
