package com.j2c.j2c.web.controller;

import com.j2c.j2c.web.test.BaseWebMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.RequestBuilder;

import static com.j2c.j2c.domain.enums.Authorities.WRITE_IMAGES;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

class ImageStorageControllerTest extends BaseWebMvcTest {

    @Test
    @WithMockUser(value = "1", authorities = WRITE_IMAGES)
    void upload_NoBody_ShouldRespond400() throws Exception {
        final RequestBuilder request = multipart("/api/images");

        assert400(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_IMAGES)
    void upload_AnyWrongFieldName_ShouldRespond400() throws Exception {
        final RequestBuilder request = multipart("/api/images")
                .file(mockImage("fyl3"));

        assert400(request);
    }

    @Test
    void upload_NotAuthenticated_ShouldRespond401() throws Exception {
        final RequestBuilder request = multipart("/api/images")
                .file(mockImage("file"));

        assert401AuthenticationRequired(request);
    }

    @Test
    @WithMockUser(value = "1")
    void upload_NotAuthorized_ShouldRespond403() throws Exception {
        final RequestBuilder request = multipart("/api/images")
                .file(mockImage("file"));

        assert403HigherPrivilegesRequired(request);
    }

    @Test
    @WithMockUser(value = "1", authorities = WRITE_IMAGES)
    void upload_HappyPath_ShouldRespond200() throws Exception {
        final RequestBuilder request = multipart("/api/images")
                .file(mockImage("file"));

        assert200(request);
    }

    private static MockMultipartFile mockImage(final String name) {
        return new MockMultipartFile(name, "puppy.jpg", "image/jpeg", new byte[0]);
    }

}
