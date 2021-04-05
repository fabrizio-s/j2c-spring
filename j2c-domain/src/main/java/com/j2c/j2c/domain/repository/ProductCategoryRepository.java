package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.ProductCategory;
import com.j2c.j2c.domain.repository.spring.ProductCategorySDJRepository;
import com.j2c.j2c.domain.repository.spring.ProductSDJRepository;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Repository
public class ProductCategoryRepository
        extends BaseRepository<ProductCategory, Long> {

    private final ProductCategorySDJRepository repository;
    private final ProductSDJRepository productRepository;

    protected ProductCategoryRepository(
            final ProductCategorySDJRepository repository,
            final ProductSDJRepository productRepository
    ) {
        super(ProductCategory.class, repository);
        this.repository = repository;
        this.productRepository = productRepository;
    }

    public Page<ProductCategory> findSubCategories(@NonNull final ProductCategory category, final Pageable pageable) {
        final int left = category.getLeft();
        final int right = category.getRight();
        return repository.findSubCategories(left, right, pageable);
    }

    public List<String> findSubCategoryFilenames(@NonNull final ProductCategory category) {
        final Long rootId = requireNonNull(category.getRootCategoryId());
        final int left = category.getLeft();
        final int right = category.getRight();
        return repository.findSubCategoryFilenames(rootId, left, right);
    }

    @Override
    public ProductCategory save(final ProductCategory category) {
        if (category != null && !category.isRootCategory() && isNew(category)) {
            return saveNewSubCategory(category);
        }
        return super.save(category);
    }

    @Override
    public void remove(final ProductCategory category) {
        if (category != null) {
            final Long rootId = category.getRootCategoryId();
            final Long categoryId = category.getId();
            if (rootId != null && categoryId != null) {
                productRepository.dereferenceProductCategory(
                        categoryId,
                        rootId,
                        category.getLeft(),
                        category.getRight()
                );
            }
        }
        super.remove(category);
    }

    private ProductCategory saveNewSubCategory(final ProductCategory subCategory) {
        final ProductCategory rootCategory = requireNonNull(subCategory.getRoot());
        final ProductCategory parentCategory = requireNonNull(subCategory.getParent());
        incrementHierarchyLeftAndRight(
                rootCategory.getId(),
                parentCategory.getId(),
                subCategory.getLeft(),
                subCategory.getRight()
        );
        return super.save(subCategory);
    }

    private void incrementHierarchyLeftAndRight(
            @NonNull final Long rootId,
            @NonNull final Long parentId,
            final int left,
            final int right
    ) {
        repository.incrementHierarchyRight(rootId, parentId, right);
        repository.incrementHierarchyLeft(rootId, parentId, left);
    }

}
