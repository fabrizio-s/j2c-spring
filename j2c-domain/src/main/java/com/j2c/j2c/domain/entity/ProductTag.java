package com.j2c.j2c.domain.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.Set;

import static com.j2c.j2c.domain.entity.MaxLengths.PRODUCTCATEGORY_NAME_MAXLENGTH;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@javax.persistence.Entity
@Table(name = "producttag")
public class ProductTag extends BaseEntity<Long> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @Column(name = "name", nullable = false,
            length = PRODUCTCATEGORY_NAME_MAXLENGTH)
    private String name;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "tag", orphanRemoval = true)
    private Set<ProductToTagAssociation> productAssociations;

    @SuppressWarnings("unused")
    ProductTag() {}

    @Builder
    private ProductTag(final String name) {
        this.name = assertNotNull(name, "name");
    }

    public void setName(final String name) {
        this.name = assertNotNull(name, "name");
    }

}
