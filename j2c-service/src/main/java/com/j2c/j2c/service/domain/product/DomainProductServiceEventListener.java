package com.j2c.j2c.service.domain.product;

import com.j2c.j2c.service.image.ImageStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Component
@RequiredArgsConstructor
public class DomainProductServiceEventListener {

    private final ImageStore imageStore;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handle(final AssignProductImagesEvent event) {
        imageStore.assignToProduct(
                event.getUploadedImageIds(),
                event.getProductId()
        );
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handle(final AssignProductCategoryImageEvent event) {
        imageStore.assignToCategory(
                event.getUploadedImageId(),
                event.getRootCategoryId()
        );
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handle(final RemoveProductImagesEvent event) {
        imageStore.removeProductImages(
                event.getProductId(),
                event.getImageFilenames()
        );
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handle(final RemoveProductCategoryImagesEvent event) {
        imageStore.removeCategoryImages(
                event.getRootCategoryId(),
                event.getImageFilenames()
        );
    }

}
