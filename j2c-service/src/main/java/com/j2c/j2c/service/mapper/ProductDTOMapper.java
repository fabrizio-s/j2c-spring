package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.Product;
import com.j2c.j2c.service.dto.ProductDTO;
import com.j2c.j2c.service.dto.ProductDTO.ProductDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
abstract class ProductDTOMapper
        extends BaseDTOMapper<ProductDTO, ProductDTOBuilder, Product> {

    @Override
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "defaultVariant.id", target = "defaultVariantId")
    @Mapping(target = "defaultVariant", ignore = true)
    @Mapping(target = "tags", ignore = true)
    public abstract ProductDTOBuilder fromEntity(Product product);

    @Override
    protected ProductDTOBuilder builder() {
        return ProductDTO.builder();
    }

}
