package com.j2c.j2c.it;

import com.fasterxml.jackson.databind.JsonNode;
import com.j2c.j2c.it.util.BaseIT;
import com.j2c.j2c.service.dto.UploadedImageDTO;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageStorageIT extends BaseIT {

    @Test
    void upload() {
        final MultiValueMap<String, Resource> body = new LinkedMultiValueMap<>();
        body.add("file", new ClassPathResource("image.png"));

        final ResponseEntity<JsonNode> response = httpRequest(
                baseUrl + "/api/images",
                HttpMethod.POST,
                body,
                adminToken
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertBodyIsOfType(response.getBody(), UploadedImageDTO.class);
    }

}
