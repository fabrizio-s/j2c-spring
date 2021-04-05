package com.j2c.j2c.service.mapper;

import com.j2c.j2c.service.dto.ProductVariantImageDTO;
import com.j2c.j2c.domain.entity.ProductVariantImage;
import com.j2c.j2c.service.dto.ProductVariantImageDTO.ProductVariantImageDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
abstract class ProductVariantImageDTOMapper
        extends BaseDTOMapper<ProductVariantImageDTO, ProductVariantImageDTOBuilder, ProductVariantImage> {

    @Override
    @Mapping(source = "variant.id", target = "variantId")
    public abstract ProductVariantImageDTOBuilder fromEntity(ProductVariantImage image);

    @Override
    protected ProductVariantImageDTOBuilder builder() {
        return ProductVariantImageDTO.builder();
    }

}
