package com.j2c.j2c.service.exception;

public final class J2cServiceErrorMessages {
    public static final String RESOURCE_NOT_FOUND = "Resource of type '%s' with id %s was not found";
    public static final String RESOURCES_NOT_FOUND = "Resources of type '%s' with the following ids were not found: %s";
    public static final String REMOVE_USER_HAS_CHECKOUT = "The user with id '%s' cannot be deleted because they have an active checkout";
    public static final String USER_EMAIL_ALREADY_EXISTS = "A user with the email '%s' already exists";

    private J2cServiceErrorMessages() {}
}
