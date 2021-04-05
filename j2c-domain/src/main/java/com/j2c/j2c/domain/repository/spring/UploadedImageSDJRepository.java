package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.UploadedImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UploadedImageSDJRepository
        extends JpaRepository<UploadedImage, UUID> {

    Optional<UploadedImage> findByFilename(String filename);

}
