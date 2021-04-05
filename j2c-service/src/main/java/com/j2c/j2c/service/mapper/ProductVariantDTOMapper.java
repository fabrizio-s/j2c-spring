package com.j2c.j2c.service.mapper;

import com.j2c.j2c.service.dto.ProductVariantDTO;
import com.j2c.j2c.service.dto.ProductVariantDTO.ProductVariantDTOBuilder;
import com.j2c.j2c.domain.entity.ProductVariant;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = ProductVariantImageDTOMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
abstract class ProductVariantDTOMapper
        extends BaseDTOMapper<ProductVariantDTO, ProductVariantDTOBuilder, ProductVariant> {

    @Override
    @Mapping(source = "product.id", target = "productId")
    @Mapping(target = "images", ignore = true)
    public abstract ProductVariantDTOBuilder fromEntity(ProductVariant variant);

    @Override
    protected ProductVariantDTOBuilder builder() {
        return ProductVariantDTO.builder();
    }

}
