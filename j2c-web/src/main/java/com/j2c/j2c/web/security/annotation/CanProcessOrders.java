package com.j2c.j2c.web.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.j2c.j2c.domain.enums.Authorities.PROCESS_ORDERS;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("authenticated and hasAuthority('" + PROCESS_ORDERS + "')")
public @interface CanProcessOrders {
}
