package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.ProductTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductTagSDJRepository
        extends JpaRepository<ProductTag, Long> {

    @Query("SELECT T.tag FROM ProductToTagAssociation T WHERE T.product.id = :productId")
    Page<ProductTag> findAllByProductId(Long productId, Pageable pageable);

}
