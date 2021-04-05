package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.ProductVariant;
import com.j2c.j2c.domain.repository.spring.ProductVariantSDJRepository;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ProductVariantRepository
        extends BaseRepository<ProductVariant, Long> {

    private final ProductVariantSDJRepository repository;

    protected ProductVariantRepository(final ProductVariantSDJRepository repository) {
        super(ProductVariant.class, repository);
        this.repository = repository;
    }

    public Page<ProductVariant> findAllByProductId(@NonNull final Long productId, final Pageable pageable) {
        return repository.findAllByProductId(productId, pageable);
    }

}
