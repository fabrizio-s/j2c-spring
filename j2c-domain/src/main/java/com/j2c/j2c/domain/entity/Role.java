package com.j2c.j2c.domain.entity;

import com.google.common.collect.ImmutableSet;
import com.j2c.j2c.domain.enums.RoleType;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

import static com.j2c.j2c.domain.entity.MaxLengths.ROLE_TYPE_MAXLENGTH;

@javax.persistence.Entity
@Table(name = "role")
public class Role extends BaseEntity<Long> {

    @Id
    @Getter
    @Column(name = "id")
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, unique = true,
            length = ROLE_TYPE_MAXLENGTH)
    private RoleType type;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_authority",
            joinColumns = @JoinColumn(name = "role_id",
                    updatable = false, nullable = false),
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT),
            inverseJoinColumns = @JoinColumn(name = "authority_id",
                    updatable = false, nullable = false),
            inverseForeignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Set<Authority> authorities;

    @SuppressWarnings("unused")
    Role() {}

    public Set<Authority> getAuthorities() {
        return ImmutableSet.copyOf(authorities);
    }

    @Override
    public String toString() {
        return "Role(" +
                "type=" + type +
                ')';
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Role role = (Role) o;
        return Objects.equals(type, role.type);
    }

}
