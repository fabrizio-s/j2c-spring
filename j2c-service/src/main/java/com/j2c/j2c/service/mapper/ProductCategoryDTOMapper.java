package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.ProductCategory;
import com.j2c.j2c.service.dto.ProductCategoryDTO;
import com.j2c.j2c.service.dto.ProductCategoryDTO.ProductCategoryDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
abstract class ProductCategoryDTOMapper
        extends BaseDTOMapper<ProductCategoryDTO, ProductCategoryDTOBuilder, ProductCategory> {

    @Override
    @Mapping(source = "category.parent.id", target = "parentId")
    @Mapping(source = "category.root.id", target = "rootId")
    public abstract ProductCategoryDTOBuilder fromEntity(ProductCategory category);

    @Override
    protected ProductCategoryDTOBuilder builder() {
        return ProductCategoryDTO.builder();
    }

}
