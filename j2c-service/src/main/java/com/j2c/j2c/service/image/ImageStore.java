package com.j2c.j2c.service.image;

import com.j2c.j2c.domain.entity.UploadedImage;

import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public interface ImageStore {

    UploadedImage store(InputStream image);

    void assignToProduct(Set<UUID> uploadedImageIds, Long productId);

    void assignToCategory(UUID uploadedImageId, Long rootCategoryId);

    void removeProductImages(Long productId, Set<String> imageFilenames);

    void removeCategoryImages(Long rootCategoryId, Set<String> imageFilenames);

}
