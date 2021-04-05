package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.UploadedImage;
import com.j2c.j2c.domain.repository.spring.UploadedImageSDJRepository;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UploadedImageRepository
        extends BaseRepository<UploadedImage, UUID> {

    private final UploadedImageSDJRepository repository;

    protected UploadedImageRepository(final UploadedImageSDJRepository repository) {
        super(UploadedImage.class, repository);
        this.repository = repository;
    }

    public Optional<UploadedImage> findByFilename(@NonNull final String filename) {
        return repository.findByFilename(filename);
    }

}
