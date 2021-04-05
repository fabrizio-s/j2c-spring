package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.UploadedImage;
import com.j2c.j2c.service.dto.UploadedImageDTO;
import com.j2c.j2c.service.dto.UploadedImageDTO.UploadedImageDTOBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class UploadedImageDTOMapper
        extends BaseDTOMapper<UploadedImageDTO, UploadedImageDTOBuilder, UploadedImage> {

    @Override
    @Mapping(target = "filename", ignore = true)
    public abstract UploadedImageDTOBuilder fromEntity(UploadedImage uploadedImage);

    @Override
    protected UploadedImageDTOBuilder builder() {
        return UploadedImageDTO.builder();
    }

}
