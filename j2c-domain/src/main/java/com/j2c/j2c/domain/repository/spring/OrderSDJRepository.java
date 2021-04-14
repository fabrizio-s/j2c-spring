package com.j2c.j2c.domain.repository.spring;

import com.j2c.j2c.domain.entity.Order;
import com.j2c.j2c.domain.entity.QOrder;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.lang.NonNull;

public interface OrderSDJRepository
        extends JpaRepository<Order, Long>,
        QuerydslPredicateExecutor<Order>,
        QuerydslBinderCustomizer<QOrder> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Order T SET T.customer = null WHERE T.customer.id = :customerId")
    void dereferenceCustomer(Long customerId);

    Page<Order> findAllByCustomerId(Long customerId, Pageable pageable);

    @Override
    @SuppressWarnings("NullableProblems")
    default void customize(@NonNull final QuerydslBindings bindings, @NonNull final QOrder order) {
        bindings.bind(order.email)
                .first(StringExpression::containsIgnoreCase);
        bindings.excluding(order.capturedAmount);
        bindings.excluding(order.previousStatus);
        bindings.excluding(order.paymentId);
        bindings.excluding(order.ipAddress);
        bindings.excluding(order.address);
        bindings.excluding(order.shippingAddress);
        bindings.excluding(order.shippingMethodDetails);
        bindings.excluding(order.lines);
        bindings.excluding(order.fulfillments);
    }

}
