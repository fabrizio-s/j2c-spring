package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.ProductToTagAssociation;
import com.j2c.j2c.domain.entity.ProductToTagAssociationPK;
import com.j2c.j2c.domain.repository.spring.ProductToTagAssociationSDJRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ProductToTagAssociationRepository
        extends BaseRepository<ProductToTagAssociation, ProductToTagAssociationPK> {

    protected ProductToTagAssociationRepository(final ProductToTagAssociationSDJRepository repository) {
        super(ProductToTagAssociation.class, repository);
    }

}
