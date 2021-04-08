package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.Role;
import com.j2c.j2c.domain.enums.RoleType;
import com.j2c.j2c.domain.exception.EntityDoesNotExistException;
import com.j2c.j2c.domain.repository.spring.RoleSDJRepository;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepository
        extends BaseRepository<Role, Long> {

    private final RoleSDJRepository repository;

    protected RoleRepository(final RoleSDJRepository repository) {
        super(Role.class, repository);
        this.repository = repository;
    }

    public Role findByType(@NonNull final RoleType type) {
        return repository.findByType(type)
                .orElseThrow(() -> new EntityDoesNotExistException(String.format("Role of type '%s' does not exist", type), this.type));
    }

}
