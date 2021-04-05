package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.ProductToTagAssociation;
import com.j2c.j2c.domain.entity.ProductToTagAssociationPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductToTagAssociationSDJRepository
        extends JpaRepository<ProductToTagAssociation, ProductToTagAssociationPK> {
}
