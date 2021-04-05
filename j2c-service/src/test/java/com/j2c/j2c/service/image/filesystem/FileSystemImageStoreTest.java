package com.j2c.j2c.service.image.filesystem;

import com.j2c.j2c.domain.entity.UploadedImage;
import com.j2c.j2c.domain.exception.EntityDoesNotExistException;
import com.j2c.j2c.domain.repository.ProductCategoryRepository;
import com.j2c.j2c.domain.repository.ProductRepository;
import com.j2c.j2c.domain.repository.UploadedImageRepository;
import com.j2c.j2c.domain.repository.spring.*;
import com.j2c.j2c.service.exception.ImageStorageException;
import com.j2c.j2c.service.test.MockEntity;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.j2c.j2c.service.image.filesystem.FileSystemImageStore.*;
import static com.j2c.j2c.service.test.TestUtils.nullable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class FileSystemImageStoreTest {

    private FileSystemImageStore imageStore;

    @Mock
    private UploadedImageSDJRepository mockUploadedImageSDJRepository;

    @Mock
    private ProductSDJRepository mockProductSDJRepository;

    @Mock
    private CheckoutLineSDJRepository mockCheckoutLineSDJRepository;

    @Mock
    private ProductCategorySDJRepository mockCategorySDJRepository;

    @Mock
    private OrderLineSDJRepository mockOrderLineSDJRepository;

    @InjectMocks
    private UploadedImageRepository uploadedImageRepository;

    @InjectMocks
    private ProductRepository productRepository;

    @InjectMocks
    private ProductCategoryRepository categoryRepository;

    private static String location;
    private static final String CLASSPATH_IMAGE_FILENAME = "image.png";
    private static final String CLASSPATH_TEXTFILE_FILENAME = "file.txt";

    @BeforeAll
    static void beforeAll() throws IOException {
        final String location = System.getenv("J2C_TEST_LOCAL_IMAGE_STORAGE_PATH");

        assertNotNull(location);
        assertFalse(location.isBlank());

        FileSystemImageStoreTest.location = Path.of(location).normalize().toRealPath().toString();
    }

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        recursivelyDeleteImageStorageLocationDirectory();

        imageStore = new FileSystemImageStore(
                Path.of(location),
                uploadedImageRepository,
                productRepository,
                categoryRepository
        );

        assertTrue(Files.isDirectory(Path.of(location))); // assert storage location directory was created
    }

    @Test
    void store_NullInputStream_ShouldThrowIllegalArgumentException() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> imageStore.store(null)
        );
        assertTrue(exception.getMessage().contains("must not be null"));
    }

    @Test
    void store_FileIsNotAnImage_ShouldThrowImageStorageException() throws IOException {
        final InputStream file = spy(getFileInClasspathAsInputStream(CLASSPATH_TEXTFILE_FILENAME));

        final Path directory = getStoreDirectory(UPLOADED_SUBDIR);

        final long currentNumberOfFilesInUploadDir = countNumberOfFilesInStoreDirectory(directory);

        final ImageStorageException exception = assertThrows(
                ImageStorageException.class,
                () -> imageStore.store(file)
        );
        assertTrue(exception.getMessage().contains("not a valid image"));
        assertEquals(currentNumberOfFilesInUploadDir, countNumberOfFilesInStoreDirectory(directory));
        verify(file, atLeastOnce()).close();
    }

    @Test
    void store_ImageHasAlreadyBeenStored_ShouldReturnExistingUploadedImageAndDeleteTempFile() throws IOException {
        final InputStream image = spy(getImageInClasspathAsInputStream());

        final Path directory = getStoreDirectory(UPLOADED_SUBDIR);

        copyImageFromClasspathToStoreDirectory(directory);

        final long currentNumberOfFilesInUploadDir = countNumberOfFilesInStoreDirectory(directory);

        final UUID existingId = mockFindUploadedImageByFilename();

        final UploadedImage uploadedImage = imageStore.store(image);

        assertEquals(existingId, uploadedImage.getId());
        assertEquals(currentNumberOfFilesInUploadDir, countNumberOfFilesInStoreDirectory(directory));
        verify(image, atLeastOnce()).close();
    }

    @Test
    void store_HappyPath_ShouldStoreImageAndReturnUploadedImageUUID() throws IOException {
        final InputStream image = spy(getImageInClasspathAsInputStream());

        final Path directory = getStoreDirectory(UPLOADED_SUBDIR);

        final long currentNumberOfFilesInUploadDir = countNumberOfFilesInStoreDirectory(directory);

        mockSaveUploadedImage();

        final UploadedImage uploadedImage = imageStore.store(image);

        assertNotNull(uploadedImage);
        assertEquals(currentNumberOfFilesInUploadDir + 1, countNumberOfFilesInStoreDirectory(directory));
        verify(image, atLeastOnce()).close();
    }

    @Test
    void assignToProduct_NullProductId_ShouldThrowIllegalArgumentException() {
        final Set<UUID> uploadedImageIds = Set.of(UUID.randomUUID());

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> imageStore.assignToProduct(uploadedImageIds, null)
        );
        assertTrue(exception.getMessage().contains("product id must not be null"));
    }

    @Test
    void assignToProduct_ProductDoesNotExist_ShouldThrowEntityDoesNotExistException() {
        final Set<UUID> uploadedImageIds = Set.of(UUID.randomUUID());
        final Long productId = 1L;

        final EntityDoesNotExistException exception = assertThrows(
                EntityDoesNotExistException.class,
                () -> imageStore.assignToProduct(uploadedImageIds, productId)
        );
        assertTrue(exception.getMessage().contains(String.format("Product with id '%s' does not exist", productId)));
    }

    @Test
    void assignToProduct_HappyPath_ShouldMoveImageToProductIdDirectory() throws IOException {
        final Set<UUID> uploadedImageIds = Set.of(UUID.randomUUID());
        final Long productId = 1L;

        final Path directory = getStoreDirectory(UPLOADED_SUBDIR);

        copyImageFromClasspathToStoreDirectory(directory);

        final long currentNumberOfFilesInUploadDir = countNumberOfFilesInStoreDirectory(directory);

        when(mockProductSDJRepository.existsById(productId))
                .thenReturn(true);

        when(mockUploadedImageSDJRepository.findAllById(anySet()))
                .thenReturn(uploadedImagesWithIds(uploadedImageIds));

        imageStore.assignToProduct(uploadedImageIds, productId);

        assertEquals(1, countNumberOfFilesInStoreDirectory(getStoreDirectory(PRODUCT_SUBDIR, productId.toString())));
        assertEquals(currentNumberOfFilesInUploadDir - 1, countNumberOfFilesInStoreDirectory(directory));
    }

    @Test
    void assignToCategory_NullRootCategoryId_ShouldThrowIllegalArgumentException() {
        final UUID uploadedImageId = UUID.randomUUID();

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> imageStore.assignToCategory(uploadedImageId, null)
        );
        assertTrue(exception.getMessage().contains("category id must not be null"));
    }

    @Test
    void assignToCategory_RootCategoryDoesNotExist_ShouldThrowEntityDoesNotExistException() {
        final UUID uploadedImageId = UUID.randomUUID();
        final Long rootCategoryId = 1L;

        final EntityDoesNotExistException exception = assertThrows(
                EntityDoesNotExistException.class,
                () -> imageStore.assignToCategory(uploadedImageId, rootCategoryId)
        );
        assertTrue(exception.getMessage().contains(String.format("Category with id '%s' does not exist", rootCategoryId)));
    }

    @Test
    void assignToCategory_HappyPath_ShouldMoveImageToRootCategoryIdDirectory() throws IOException {
        final UUID uploadedImageId = UUID.randomUUID();
        final Long rootCategoryId = 1L;

        final Path directory = getStoreDirectory(UPLOADED_SUBDIR);

        copyImageFromClasspathToStoreDirectory(directory);

        final long currentNumberOfFilesInUploadDir = countNumberOfFilesInStoreDirectory(directory);

        when(mockCategorySDJRepository.existsById(rootCategoryId))
                .thenReturn(true);

        when(mockUploadedImageSDJRepository.findById(uploadedImageId))
                .thenReturn(Optional.of(uploadedImageWithId(uploadedImageId)));

        imageStore.assignToCategory(uploadedImageId, rootCategoryId);

        assertEquals(1, countNumberOfFilesInStoreDirectory(getStoreDirectory(CATEGORY_SUBDIR, rootCategoryId.toString())));
        assertEquals(currentNumberOfFilesInUploadDir - 1, countNumberOfFilesInStoreDirectory(directory));
    }

    @Test
    void removeProductImages_NullProductId_ShouldThrowIllegalArgumentException() {
        final Set<String> imageFilenames = Set.of(CLASSPATH_IMAGE_FILENAME);

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> imageStore.removeProductImages(null, imageFilenames)
        );
        assertTrue(exception.getMessage().contains("product id must not be null"));
    }

    @Test
    void removeProductImages_HappyPath_ShouldDeleteImage() throws IOException {
        final Long productId = 1L;
        final Set<String> imageFilenames = Set.of(CLASSPATH_IMAGE_FILENAME);

        final Path directory = Files.createDirectories(getStoreDirectory(PRODUCT_SUBDIR, productId.toString()));

        copyImageFromClasspathToStoreDirectory(directory);

        final long currentNumberOfFilesInProductIdDir = countNumberOfFilesInStoreDirectory(directory);

        imageStore.removeProductImages(productId, imageFilenames);

        assertEquals(currentNumberOfFilesInProductIdDir - 1, countNumberOfFilesInStoreDirectory(directory));
    }

    @Test
    void removeCategoryImages_NullRootCategoryId_ShouldThrowIllegalArgumentException() {
        final Set<String> imageFilenames = Set.of(CLASSPATH_IMAGE_FILENAME);

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> imageStore.removeCategoryImages(null, imageFilenames)
        );
        assertTrue(exception.getMessage().contains("root category id must not be null"));
    }

    @Test
    void removeCategoryImages_HappyPath_ShouldDeleteImage() throws IOException {
        final Long rootCategoryId = 1L;
        final Set<String> imageFilenames = Set.of(CLASSPATH_IMAGE_FILENAME);

        final Path directory = Files.createDirectories(getStoreDirectory(CATEGORY_SUBDIR, rootCategoryId.toString()));

        copyImageFromClasspathToStoreDirectory(directory);

        final long currentNumberOfFilesInRootCategoryIdDir = countNumberOfFilesInStoreDirectory(directory);

        imageStore.removeCategoryImages(rootCategoryId, imageFilenames);

        assertEquals(currentNumberOfFilesInRootCategoryIdDir - 1, countNumberOfFilesInStoreDirectory(directory));
    }

    private void mockSaveUploadedImage() {
        when(mockUploadedImageSDJRepository.save(isNotNull()))
                .thenAnswer(i -> {
                    final UploadedImage argument = (UploadedImage) i.getArguments()[0];
                    if (argument.getId() == null) {
                        setField(argument, "id", UUID.randomUUID());
                    }
                    return argument;
                });
    }

    private UUID mockFindUploadedImageByFilename() {
        final UUID existingId = UUID.randomUUID();
        when(mockUploadedImageSDJRepository.findByFilename(anyString()))
                .thenAnswer(i -> {
                    final String filename = (String) i.getArguments()[0];
                    return Optional.of(
                            MockEntity.uploadedImage()
                                    .id(existingId)
                                    .filename(filename)
                                    .build()
                    );
                });
        return existingId;
    }

    private static List<UploadedImage> uploadedImagesWithIds(final Set<UUID> ids) {
        return nullable(ids)
                .map(FileSystemImageStoreTest::uploadedImageWithId)
                .collect(Collectors.toList());
    }

    private static UploadedImage uploadedImageWithId(final UUID id) {
        return MockEntity.uploadedImage()
                .id(id)
                .filename(CLASSPATH_IMAGE_FILENAME)
                .build();
    }

    private static InputStream getImageInClasspathAsInputStream() {
        return getFileInClasspathAsInputStream(CLASSPATH_IMAGE_FILENAME);
    }

    private static InputStream getFileInClasspathAsInputStream(final String filename) {
        return FileSystemImageStoreTest.class.getClassLoader().getResourceAsStream(filename);
    }

    private static void copyImageFromClasspathToStoreDirectory(final Path targetDir) throws IOException {
        final InputStream is = getImageInClasspathAsInputStream();
        final Path tmp = Files.createTempFile(targetDir, null, null);
        Files.copy(is, tmp, StandardCopyOption.REPLACE_EXISTING);
        Files.move(tmp, tmp.resolveSibling(CLASSPATH_IMAGE_FILENAME), StandardCopyOption.REPLACE_EXISTING);
    }

    private static Path getStoreDirectory(@NonNull final String... directories) {
        Path directory = Path.of(location);
        for (final String dirname : directories) {
            directory = directory.resolve(dirname);
        }
        return directory;
    }

    private static long countNumberOfFilesInStoreDirectory(final Path directory) throws IOException {
        try (final Stream<Path> files = Files.list(directory)) {
            return files.count();
        }
    }

    private static void recursivelyDeleteImageStorageLocationDirectory() throws IOException {
        Files.walkFileTree(Path.of(location), new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
    }

}
