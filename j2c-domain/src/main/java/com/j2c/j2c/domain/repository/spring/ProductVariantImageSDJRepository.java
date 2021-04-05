package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.ProductVariantImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductVariantImageSDJRepository
        extends JpaRepository<ProductVariantImage, Long> {

    Page<ProductVariantImage> findAllByVariantId(Long variantId, Pageable pageable);

    @Query("SELECT T.filename FROM ProductVariantImage T WHERE T.variant.product.id = :productId")
    List<String> findFilenamesByProductId(Long productId);

    @Query("SELECT T.filename FROM ProductVariantImage T WHERE T.variant.id = :variantId")
    List<String> findFilenamesByVariantId(Long variantId);

}
