package com.j2c.j2c.domain.repository.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ProductCategorySDJRepositoryTest {

    @Autowired
    private ProductCategorySDJRepository repository;

    @Test
    void findSubCategoryFilenames() {
        final List<String> filenames = repository.findSubCategoryFilenames(1L, 4, 11);

        assertEquals(3, filenames.size());
        assertTrue(filenames.contains("doge04.jpg"));
        assertTrue(filenames.contains("doge05.jpg"));
        assertTrue(filenames.contains("doge06.jpg"));
    }

}
