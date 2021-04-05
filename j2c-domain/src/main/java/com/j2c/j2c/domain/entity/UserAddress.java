package com.j2c.j2c.domain.entity;

import com.j2c.j2c.domain.exception.DomainException;
import lombok.*;

import javax.persistence.*;

import static com.j2c.j2c.domain.exception.DomainErrorMessages.ADDRESS_DOES_NOT_BELONG_TO_USER;
import static com.j2c.j2c.domain.util.J2cUtils.assertNotNull;

@javax.persistence.Entity
@Table(name = "useraddress",
        indexes = @Index(columnList = "user_id")
)
public class UserAddress extends BaseEntity<Long> {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Getter
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private User user;

    @Getter
    @Embedded
    private Address address;

    @SuppressWarnings("unused")
    UserAddress() {}

    @Builder
    private UserAddress(
            final User user,
            final Address address
    ) {
        this.user = assertNotNull(user, "user");
        this.address = assertNotNull(address, "address");
    }

    public UserAddress verifyBelongsToUser(final User user) {
        if (!belongsToUser(user)) {
            throw new DomainException(String.format(ADDRESS_DOES_NOT_BELONG_TO_USER, id, user.getId()), this);
        }
        return this;
    }

    public boolean belongsToUser(final User user) {
        if (user == null) {
            return false;
        }
        return user.isSameAs(this.user);
    }

    public void setAddress(final Address address) {
        this.address = assertNotNull(address, "address");
    }

}
