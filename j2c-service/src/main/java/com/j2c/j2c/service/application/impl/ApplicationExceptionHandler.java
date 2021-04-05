package com.j2c.j2c.service.application.impl;

import com.google.common.collect.ImmutableSet;
import com.j2c.j2c.service.exception.*;
import com.j2c.j2c.service.util.ExceptionMessageUtils;
import com.j2c.j2c.domain.exception.DomainException;
import com.j2c.j2c.domain.exception.EntitiesDoNotExistException;
import com.j2c.j2c.domain.exception.EntityDoesNotExistException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;
import java.util.Set;
import java.util.stream.Collectors;

import static com.j2c.j2c.service.exception.J2cServiceErrorMessages.*;
import static com.j2c.j2c.service.util.ExceptionMessageUtils.entityTypeName;

@Slf4j
@Aspect
@Component
public class ApplicationExceptionHandler {

    @Around("execution(* com.j2c.j2c.service.application.*Service.*(..))")
    public Object handle(final ProceedingJoinPoint jp) {
        try {
            return jp.proceed();
        }
        catch (final ConstraintViolationException ex) {
            final Set<String> errors = ex.getConstraintViolations().stream()
                    .map(ExceptionMessageUtils::constraintViolationFormattedMessage)
                    .collect(ImmutableSet.toImmutableSet());
            throw new InvalidInputException(errors, ex.getConstraintViolations(), ex);
        }
        catch (final EntityDoesNotExistException ex) {
            String message = ex.getCustomErrorMessage();
            if (message == null) {
                final String entityTypeName = entityTypeName(ex.getEntityType());
                message = String.format(RESOURCE_NOT_FOUND, entityTypeName, ex.getId());
                throw new ResourceNotFoundException(message, ex);
            }
            throw new ResourceNotFoundException(message, ex);
        }
        catch (final EntitiesDoNotExistException ex) {
            final String entityTypeName = entityTypeName(ex.getEntityType());
            final String ids = ex.getIds().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
            final String message = String.format(RESOURCES_NOT_FOUND, entityTypeName, ids);
            throw new ResourceNotFoundException(message, ex);
        }
        catch (final DomainException ex) {
            throw new ServiceException(ex.getMessage(), ex);
        }
        catch (final ServiceException | ResourceNotFoundException | ResourceAlreadyExistsException | ImageStorageException | GatewayException ex) {
            throw ex;
        }
        catch (final Throwable ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

}
