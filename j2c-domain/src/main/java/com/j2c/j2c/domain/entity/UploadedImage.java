package com.j2c.j2c.domain.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.UUID;

import static com.j2c.j2c.domain.entity.MaxLengths.IMAGEFILENAME_MAXLENGTH;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@javax.persistence.Entity
@Table(name = "uploadedimage")
public class UploadedImage extends OnCreateAuditedEntity<UUID> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Getter
    @Column(name = "filename", nullable = false, updatable = false,
            unique = true, length = IMAGEFILENAME_MAXLENGTH)
    private String filename;

    @SuppressWarnings("unused")
    UploadedImage() {}

    @Builder
    private UploadedImage(final String filename) {
        this.filename = assertNotNull(filename, "filename");
    }

    public void setFilename(final String filename) {
        this.filename = assertNotNull(filename, "filename");
    }

}
