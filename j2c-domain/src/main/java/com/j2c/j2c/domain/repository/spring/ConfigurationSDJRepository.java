package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.Configuration;
import com.j2c.j2c.domain.enums.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigurationSDJRepository
        extends JpaRepository<Configuration, Long> {

    Optional<Configuration> findByProfile(Profile profile);

}
