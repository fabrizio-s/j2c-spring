package com.j2c.j2c.domain.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@MappedSuperclass
public abstract class OnCreateAuditedEntity<ID> extends BaseEntity<ID> {

    @Getter
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = now();
    }

}
