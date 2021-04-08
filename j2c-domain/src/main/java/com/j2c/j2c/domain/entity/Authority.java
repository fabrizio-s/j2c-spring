package com.j2c.j2c.domain.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

import static com.j2c.j2c.domain.entity.MaxLengths.AUTHORITY_VALUE_MAXLENGTH;

@javax.persistence.Entity
@Table(name = "authority")
public class Authority extends BaseEntity<Long> {

    @Id
    @Getter
    @Column(name = "id")
    private Long id;

    @Getter
    @Column(name = "value", nullable = false, updatable = false,
            unique = true, length = AUTHORITY_VALUE_MAXLENGTH)
    private String value;

    @SuppressWarnings("unused")
    Authority() {}

    @Override
    public String toString() {
        return "Authority(" +
                "value='" + value + '\'' +
                ')';
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Authority authority = (Authority) o;
        return Objects.equals(value, authority.value);
    }

}
