package com.j2c.j2c.service.mapper;

import com.j2c.j2c.domain.entity.UploadedImage;
import com.j2c.j2c.service.dto.UploadedImageDTO;
import com.j2c.j2c.service.test.BaseMapperTest;
import org.junit.jupiter.api.Test;

import static com.j2c.j2c.service.test.TestUtils.nextObject;
import static org.junit.jupiter.api.Assertions.*;
import static org.mapstruct.factory.Mappers.getMapper;

class UploadedImageDTOMapperTest extends BaseMapperTest {

    @Test
    void fromEntity() {
        final UploadedImage entity = nextObject(UploadedImage.class);

        final UploadedImageDTO dto = getMapper(UploadedImageDTOMapper.class).fromEntity(entity).build();

        assertMappings(entity, dto, "filename");
        assertNull(dto.getFilename());
    }

}
