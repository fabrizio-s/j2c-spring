package com.j2c.j2c.web.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fasterxml.jackson.databind.cfg.ConstructorDetector.USE_PROPERTIES_BASED;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomizer() {
        // 'setConstructorDetector(USE_PROPERTIES_BASED)' fixes:
        //         ''MismatchedInputException: Cannot construct instance of (although at least
        //         one Creator exists): cannot deserialize from Object value (no delegate- or
        //         property-based Creator)''
        // for forms with only one field.
        return builder -> builder
                .featuresToEnable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .postConfigurer(mapper -> mapper.setConstructorDetector(USE_PROPERTIES_BASED));
    }

}
