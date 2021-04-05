package com.j2c.j2c.service.application;

import com.j2c.j2c.service.dto.UploadedImageDTO;

import javax.validation.constraints.NotNull;
import java.io.InputStream;

public interface ImageStorageService {

    UploadedImageDTO store(@NotNull InputStream image);

}
