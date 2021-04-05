package com.j2c.j2c.domain.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@MappedSuperclass
public abstract class OnUpdateAuditedEntity<ID> extends OnCreateAuditedEntity<ID> {

    @Getter
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = now();
    }

}
