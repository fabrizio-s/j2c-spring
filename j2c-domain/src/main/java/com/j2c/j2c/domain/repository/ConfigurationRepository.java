package com.j2c.j2c.domain.repository;

import com.j2c.j2c.domain.entity.Configuration;
import com.j2c.j2c.domain.enums.Profile;
import com.j2c.j2c.domain.exception.EntityDoesNotExistException;
import com.j2c.j2c.domain.repository.spring.ConfigurationSDJRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ConfigurationRepository
        extends BaseRepository<Configuration, Long> {

    private final ConfigurationSDJRepository repository;

    protected ConfigurationRepository(final ConfigurationSDJRepository repository) {
        super(Configuration.class, repository);
        this.repository = repository;
    }

    public Configuration getConfiguration() {
        return repository.findByProfile(Profile.DEFAULT)
                .orElseThrow(() -> new EntityDoesNotExistException("No default configuration exists", type));
    }

}
