package com.j2c.j2c.service.application.impl;

import com.j2c.j2c.domain.entity.UploadedImage;
import com.j2c.j2c.service.application.ImageStorageService;
import com.j2c.j2c.service.dto.UploadedImageDTO;
import com.j2c.j2c.service.image.ImageStore;
import com.j2c.j2c.service.mapper.UploadedImageDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.InputStream;

@Service
@Validated
@RequiredArgsConstructor
public class ImageStorageServiceImpl implements ImageStorageService {

    private final ImageStore store;
    private final UploadedImageDTOMapper mapper;

    @Override
    public UploadedImageDTO store(@NotNull final InputStream image) {
        final UploadedImage uploadedImage = store.store(image);
        return mapper.fromEntity(uploadedImage).build();
    }

}
