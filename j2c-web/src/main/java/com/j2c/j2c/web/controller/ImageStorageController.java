package com.j2c.j2c.web.controller;

import com.j2c.j2c.service.application.ImageStorageService;
import com.j2c.j2c.service.dto.UploadedImageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.j2c.j2c.domain.enums.Authorities.WRITE_IMAGES;

@RestController
@RequiredArgsConstructor
@Tag(name = "Images", description = "Endpoints related to image upload")
public class ImageStorageController {

    private final ImageStorageService imageStorageService;

    @PostMapping(value = "/api/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("authenticated and hasAuthority('" + WRITE_IMAGES + "')")
    @Operation(security = @SecurityRequirement(name = "JWT"),
            summary = "Upload an image",
            description = "Returns the id of the created resource. " +
                    "PNG and JPG are the only valid formats. " +
                    "If the image has already been uploaded, the same id will be returned. " +
                    "Requires " + WRITE_IMAGES + " authority (Admin).")
    public UploadedImageDTO upload(@RequestParam("file") final MultipartFile file) throws IOException {
        return imageStorageService.store(file.getInputStream());
    }

}
