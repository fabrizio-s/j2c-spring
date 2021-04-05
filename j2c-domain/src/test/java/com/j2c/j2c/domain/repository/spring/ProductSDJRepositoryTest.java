package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.Product;
import com.j2c.j2c.domain.entity.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductSDJRepositoryTest {

    @Autowired
    private ProductSDJRepository repository;

    @Test
    void findAll() {
        System.out.println();
        final BooleanExpression predicate = QProduct.product.isNotNull();

        final Page<Product> page = repository.findAll(predicate, PageRequest.of(0, 20));
        System.out.println("page.getTotalPages() = " + page.getTotalPages());
        System.out.println("page.getTotalElements() = " + page.getTotalElements());
        page.getContent().forEach(System.out::println);
        System.out.println();
    }

}
