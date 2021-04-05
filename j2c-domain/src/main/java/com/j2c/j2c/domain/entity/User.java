package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.exception.DomainException;
import lombok.*;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.j2c.j2c.domain.entity.MaxLengths.*;
import static com.j2c.j2c.domain.exception.DomainErrorMessages.VALID_USER_EMAIL_BUT_NULL_PASSWORD;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;
import static java.time.LocalDateTime.now;

@javax.persistence.Entity
@Table(name = "`user`",
        indexes = {
                @Index(columnList = "role_id")
        }
)
public class User extends OnCreateAuditedEntity<Long> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @Column(name = "email", unique = true,
            length = EMAIL_MAXLENGTH)
    private String email;

    @Getter
    @Column(name = "password",
            length = USER_PASSWORD_MAXLENGTH)
    private String password;

    @Getter
    @Setter
    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Getter
    @Column(name = "verified", nullable = false)
    private boolean verified;

    @Getter
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Getter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Role role;

    @Getter
    @Setter
    @Column(name = "external_id",
            length = USER_EXTERNALID_MAXLENGTH)
    private String externalId;

    @Getter
    @Setter
    @Column(name = "default_payment_method_id",
            length = USER_EXTERNALID_MAXLENGTH)
    private String defaultPaymentMethodId;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,
            mappedBy = "user", orphanRemoval = true)
    private List<UserAddress> addresses = new ArrayList<>();

    @SuppressWarnings("unused")
    User() {}

    @Builder
    private User(
            final String email,
            final String password,
            final boolean enabled,
            final boolean verified,
            final Role role
    ) {
        this.password = password;
        if (email != null) {
            setEmail(email);
        }
        this.enabled = enabled;
        this.verified = verified;
        this.role = assertNotNull(role, "role");
    }

    public UserAddress addAddress(final Address address) {
        final UserAddress userAddress = UserAddress.builder()
                .user(this)
                .address(
                        assertNotNull(address, "address")
                                .copy()
                                .build()
                )
                .build();
        addresses.add(userAddress);
        return userAddress;
    }

    public void removeAddress(final UserAddress address) {
        if (address == null) {
            return;
        }
        addresses.remove(address);
    }

    public void setVerified(final boolean verified) {
        if (verified) {
            verifiedAt = now();
        }
        this.verified = verified;
    }

    public void setEmail(final String email) {
        if (password == null) {
            throw new DomainException(String.format(VALID_USER_EMAIL_BUT_NULL_PASSWORD, id), this);
        }
        this.email = assertNotNull(email, "email");
    }

    public void setPassword(final String password) {
        this.password = assertNotNull(password, "password");
    }

    public void setRole(final Role role) {
        this.role = assertNotNull(role, "role");
    }

}
