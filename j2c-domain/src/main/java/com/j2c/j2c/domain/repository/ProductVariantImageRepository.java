package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.ProductVariantImage;
import com.j2c.j2c.domain.repository.spring.ProductVariantImageSDJRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductVariantImageRepository
        extends BaseRepository<ProductVariantImage, Long> {

    private final ProductVariantImageSDJRepository repository;

    protected ProductVariantImageRepository(final ProductVariantImageSDJRepository repository) {
        super(ProductVariantImage.class, repository);
        this.repository = repository;
    }

    public Page<ProductVariantImage> findAllByVariantId(@NonNull final Long variantId, final Pageable pageable) {
        return repository.findAllByVariantId(variantId, pageable);
    }

    public List<String> findFilenamesByProductId(@NonNull final Long productId) {
        return repository.findFilenamesByProductId(productId);
    }

    public List<String> findFilenamesByVariantId(@NonNull final Long variantId) {
        return repository.findFilenamesByVariantId(variantId);
    }

}
