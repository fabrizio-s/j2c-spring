package com.j2c.j2c.web.config;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Configuration
@ConditionalOnProperty(
        prefix="j2c.storage.image.filesystem",
        name="location")
public class FileSystemImageStoreConfig implements WebMvcConfigurer {

    @Value("${j2c.service.storage.image.filesystem.location}")
    private String location;

    @Override
    public void addResourceHandlers(@NonNull final ResourceHandlerRegistry registry) {
        // expose static images over http if property exists
        if (!location.isEmpty()) {
            registry
                    .addResourceHandler("/media/**")
                    .addResourceLocations(validate(location));
        }
    }

    private static String validate(final String path) {
        try {
            String location = Path.of(path).normalize().toRealPath().toString();
            if (!location.startsWith("file:")) {
                location = "file:" + location;
            }
            if (!location.endsWith(File.separator)) {
                location += File.separatorChar;
            }
            return location;
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

}
