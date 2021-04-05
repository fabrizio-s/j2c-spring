package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.Product;
import com.j2c.j2c.domain.entity.QProduct;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.j2c.j2c.domain.repository.spring.ProductCategorySDJRepository.FIND_SUBCATEGORY_IDS;
import static java.util.stream.Collectors.toList;

public interface ProductSDJRepository
        extends JpaRepository<Product, Long>,
                QuerydslPredicateExecutor<Product>,
                QuerydslBinderCustomizer<QProduct> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Product T SET T.category = null WHERE T.category.id = :categoryId OR T.category.id IN " +
            "(" + FIND_SUBCATEGORY_IDS + ")")
    void dereferenceProductCategory(Long categoryId, Long rootId, Integer left, Integer right);

    @Override
    @SuppressWarnings("NullableProblems")
    default void customize(@NonNull final QuerydslBindings bindings, @NonNull final QProduct product) {
        bindings.bind(product.name)
                .first(StringExpression::containsIgnoreCase);
        bindings.bind(product.category)
                .all(
                        (categoryRoot, categories) ->
                                categories.stream()
                                        .filter(Objects::nonNull)
                                        .map(
                                                c -> (Predicate) categoryRoot.root.id.eq(c.getRootCategoryId())
                                                        .and(categoryRoot.left.goe(c.getLeft()))
                                                        .and(categoryRoot.right.loe(c.getRight()))
                                        )
                                        .collect(Collectors.collectingAndThen(toList(), predicates -> Optional.ofNullable(ExpressionUtils.anyOf(predicates))))

                );
        bindings.bind(product.tagAssociations.any().tag)
                .as("tag")
                .all(
                        (tagRoot, tags) ->
                                tags.stream()
                                        .filter(Objects::nonNull)
                                        .map(t -> (Predicate) tagRoot.id.eq(t.getId()))
                                        .collect(Collectors.collectingAndThen(toList(), predicates -> Optional.ofNullable(ExpressionUtils.anyOf(predicates))))
                );
        bindings.excluding(product.description);
        bindings.excluding(product.imageFilename);
    }

}
