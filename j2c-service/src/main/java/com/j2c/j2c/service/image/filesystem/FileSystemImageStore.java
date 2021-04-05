package com.j2c.j2c.service.image.filesystem;

import com.j2c.j2c.domain.entity.UploadedImage;
import com.j2c.j2c.domain.repository.ProductCategoryRepository;
import com.j2c.j2c.domain.repository.ProductRepository;
import com.j2c.j2c.domain.repository.UploadedImageRepository;
import com.j2c.j2c.service.exception.ImageStorageException;
import com.j2c.j2c.service.image.ImageStore;
import lombok.NonNull;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional
class FileSystemImageStore implements ImageStore {

    private final Path baseDir;
    private final UploadedImageRepository uploadedImageRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;

    private static final String INVALID_DIR = "'%s' is not a valid directory";
    private static final String NO_READ_ACCESS = "No read access for directory %s";
    private static final String NO_WRITE_ACCESS = "No write access for directory %s";
    static final String PRODUCT_SUBDIR = "product";
    static final String CATEGORY_SUBDIR = "category";
    static final String UPLOADED_SUBDIR = "uploaded";
    private static final Set<String> validMimeTypes = Set.of("image/jpeg", "image/png");

    public FileSystemImageStore(
            @NonNull @Value("${j2c.service.storage.image.filesystem.location}") final Path baseDir,
            final UploadedImageRepository uploadedImageRepository,
            final ProductRepository productRepository,
            final ProductCategoryRepository categoryRepository
    ) throws IOException {
        if (!Files.exists(baseDir)) {
            this.baseDir = Files.createDirectories(baseDir);
        } else {
            validateDirectory(baseDir);
            this.baseDir = baseDir;
        }
        createSubDirectoryIfNotExists(PRODUCT_SUBDIR);
        createSubDirectoryIfNotExists(CATEGORY_SUBDIR);
        createSubDirectoryIfNotExists(UPLOADED_SUBDIR);
        this.uploadedImageRepository = uploadedImageRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public UploadedImage store(final InputStream image) {
        if (image == null) {
            throw new IllegalArgumentException("input stream must not be null");
        }
        try (image) {
            final Path tmp = createTempFile();
            final String md5sum = copyFileAndCalculateMD5Hash(image, tmp);
            final String mimeType = getMimeType(tmp);
            final Optional<UploadedImage> optional = uploadedImageRepository.findByFilename(md5sum);
            if (optional.isPresent()) {
                Files.delete(tmp);
                return optional.get();
            } else if (!isValidMimeType(mimeType)) {
                Files.delete(tmp);
                throw new ImageStorageException(String.format("File with mime type '%s' is not a valid image", mimeType));
            }
            final Path file = renameFile(tmp, md5sum);
            return uploadedImageRepository.save(
                    UploadedImage.builder()
                            .filename(file.getFileName().toString())
                            .build()
            );
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Async
    @Override
    public void assignToProduct(
            final Set<UUID> uploadedImageIds,
            final Long productId
    ) {
        if (productId == null) {
            throw new IllegalArgumentException("product id must not be null");
        } else if (uploadedImageIds == null || uploadedImageIds.isEmpty()) {
            return;
        }
        try {
            productRepository.verifyExistsById(productId);
            final Set<String> filenames = findAndRemove(uploadedImageIds);
            final Path uploadedSubDir = resolveUploadedSubDir();
            final Path targetDir = createNewProductIdDirIfNotExists(productId);
            moveFiles(uploadedSubDir, filenames, targetDir);
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Async
    @Override
    public void assignToCategory(
            final UUID uploadedImageId,
            final Long rootCategoryId
    ) {
        if (rootCategoryId == null) {
            throw new IllegalArgumentException("root category id must not be null");
        } else if (uploadedImageId == null) {
            return;
        }
        try {
            categoryRepository.verifyExistsById(rootCategoryId);
            final String filename = findAndRemove(uploadedImageId);
            final Path uploadedSubDir = resolveUploadedSubDir();
            final Path targetDir = createNewProductCategoryIdDirIfNotExists(rootCategoryId);
            moveFiles(uploadedSubDir, Collections.singleton(filename), targetDir);
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Async
    @Override
    public void removeProductImages(
            final Long productId,
            final Set<String> imageFilenames
    ) {
        if (productId == null) {
            throw new IllegalArgumentException("product id must not be null");
        } else if (imageFilenames == null || imageFilenames.isEmpty()) {
            return;
        }
        final Path productIdSubDir = getSubDirectory(PRODUCT_SUBDIR, productId.toString());
        deleteFiles(productIdSubDir, imageFilenames);
    }

    @Async
    @Override
    public void removeCategoryImages(
            final Long rootCategoryId,
            final Set<String> imageFilenames
    ) {
        if (rootCategoryId == null) {
            throw new IllegalArgumentException("root category id must not be null");
        } else if (imageFilenames == null || imageFilenames.isEmpty()) {
            return;
        }
        final Path categoryIdSubDir = getSubDirectory(CATEGORY_SUBDIR, rootCategoryId.toString());
        deleteFiles(categoryIdSubDir, imageFilenames);
    }

    private Path resolveUploadedSubDir() {
        validateDirectory(baseDir);
        final Path uploadedSubDir = baseDir.resolve(UPLOADED_SUBDIR);
        validateDirectory(uploadedSubDir);
        return uploadedSubDir;
    }

    private String findAndRemove(final UUID uploadedImageId) {
        final UploadedImage uploadedImage = uploadedImageRepository.findById(uploadedImageId);
        final String filename = uploadedImage.getFilename();
        uploadedImageRepository.remove(uploadedImage);
        return filename;
    }

    private Set<String> findAndRemove(final Set<UUID> uploadedImageIds) {
        final List<UploadedImage> uploadedImages = uploadedImageRepository.findAllByIdDoNotThrow(
                uploadedImageIds.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );
        final Set<String> filenames = uploadedImages.stream()
                .map(UploadedImage::getFilename)
                .collect(Collectors.toSet());
        uploadedImageRepository.removeAll(uploadedImages);
        return filenames;
    }

    private Path createTempFile() throws IOException {
        final Path uploadedSubDir = createSubDirectoryIfNotExists(UPLOADED_SUBDIR);
        return Files.createTempFile(uploadedSubDir, "j2c-", null);
    }

    private Path createNewProductIdDirIfNotExists(final Long productId) throws IOException {
        final Path productSubDir = createSubDirectoryIfNotExists(PRODUCT_SUBDIR);
        return createSubDirectoryIfNotExists(productSubDir, String.valueOf(productId));
    }

    private Path createNewProductCategoryIdDirIfNotExists(final Long categoryId) throws IOException {
        final Path categorySubDir = createSubDirectoryIfNotExists(CATEGORY_SUBDIR);
        return createSubDirectoryIfNotExists(categorySubDir, String.valueOf(categoryId));
    }

    private Path createSubDirectoryIfNotExists(final String dirName) throws IOException {
        return createSubDirectoryIfNotExists(baseDir, dirName);
    }

    private Path getSubDirectory(final String... directories) {
        Path directory = baseDir;
        for (final String dirname : directories) {
            directory = directory.resolve(dirname);
        }
        return directory;
    }

    private static Path renameFile(final Path tmp, final String newFilename) throws IOException {
        return Files.move(tmp, tmp.resolveSibling(newFilename), StandardCopyOption.REPLACE_EXISTING);
    }

    private static String copyFileAndCalculateMD5Hash(final InputStream is, final Path tmp) throws IOException {
        final MessageDigest md = newMD5();
        try (final DigestInputStream dis = new DigestInputStream(is, md)) {
            Files.copy(dis, tmp, StandardCopyOption.REPLACE_EXISTING);
        }
        return DigestUtils.md5DigestAsHex(md.digest());
    }

    private static void moveFiles(final Path sourceDir, final Set<String> filenames, final Path targetDir) throws IOException {
        filenames.stream()
                .map(sourceDir::resolve)
                .filter(Files::exists)
                .filter(Files::isRegularFile)
                .forEach(f -> moveFile(f, targetDir));
    }

    private static void moveFile(final Path file, final Path targetDir) {
        try {
            Files.move(file, targetDir.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static void deleteFiles(final Path location, final Set<String> filenames) {
        if (!Files.isDirectory(location)) {
            return;
        }
        filenames.stream()
                .filter(Objects::nonNull)
                .map(location::resolve)
                .filter(Files::isRegularFile)
                .forEach(FileSystemImageStore::deleteFile);
    }

    private static void deleteFile(final Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static String getMimeType(final Path file) throws IOException {
        return new Tika().detect(file);
    }

    private static Path createSubDirectoryIfNotExists(final Path parentDir, final String dirName) throws IOException {
        final Path dir = parentDir.resolve(dirName);
        if (!Files.exists(dir)) {
            Files.createDirectory(dir);
        }
        validateDirectory(dir);
        return dir;
    }

    private static boolean isValidMimeType(final String mimeType) {
        if (mimeType == null) {
            return false;
        }
        return validMimeTypes.contains(mimeType);
    }

    private static void validateDirectory(final Path dir) {
        if (!Files.isDirectory(dir)) {
            throw new ImageStorageException(String.format(INVALID_DIR, dir));
        } else if (!Files.isReadable(dir)) {
            throw new ImageStorageException(String.format(NO_READ_ACCESS, dir));
        } else if (!Files.isWritable(dir)) {
            throw new ImageStorageException(String.format(NO_WRITE_ACCESS, dir));
        }
    }

    private static MessageDigest newMD5() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (final NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }

}
