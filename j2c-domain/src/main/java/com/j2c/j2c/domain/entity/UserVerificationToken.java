package com.j2c.j2c.domain.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.UUID;

import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@javax.persistence.Entity
@Table(name = "userverificationtoken")
public class UserVerificationToken extends OnCreateAuditedEntity<UUID> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Getter
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false, unique = true,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private User user;

    @SuppressWarnings("unused")
    UserVerificationToken() {}

    @Builder
    private UserVerificationToken(final User user) {
        this.user = assertNotNull(user, "user");
    }

}
