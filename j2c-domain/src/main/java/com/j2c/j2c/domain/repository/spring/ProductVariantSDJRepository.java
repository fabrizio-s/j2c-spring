package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantSDJRepository
        extends JpaRepository<ProductVariant, Long> {

    Page<ProductVariant> findAllByProductId(Long productId, Pageable pageable);

}
