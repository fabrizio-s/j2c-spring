package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.ProductTag;
import com.j2c.j2c.service.dto.ProductTagDTO;
import com.j2c.j2c.service.dto.ProductTagDTO.ProductTagDTOBuilder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
abstract class ProductTagDTOMapper
        extends BaseDTOMapper<ProductTagDTO, ProductTagDTOBuilder, ProductTag> {

    @Override
    public abstract ProductTagDTOBuilder fromEntity(ProductTag tag);

    @Override
    protected ProductTagDTOBuilder builder() {
        return ProductTagDTO.builder();
    }

}
