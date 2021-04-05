package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.ProductTag;
import com.j2c.j2c.domain.repository.spring.ProductTagSDJRepository;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ProductTagRepository
        extends BaseRepository<ProductTag, Long> {

    private final ProductTagSDJRepository repository;

    protected ProductTagRepository(final ProductTagSDJRepository repository) {
        super(ProductTag.class, repository);
        this.repository = repository;
    }

    public Page<ProductTag> findAllByProductId(@NonNull final Long productId, final Pageable pageable) {
        return repository.findAllByProductId(productId, pageable);
    }

}
