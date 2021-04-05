package com.j2c.j2c.service.input;

public final class RegExp {
    public static final String ADDRESS_NAME_PATTERN = "^([A-Za-z]+(\\s|'))*[A-Za-z]+$";
    public static final String ADDRESS_PHONE_PATTERN = "^\\+?[0-9]+$";
    public static final String ADDRESS_POSTALCODE_PATTERN = "^[a-zA-Z0-9][a-zA-Z0-9\\- ]{0,10}[a-zA-Z0-9]$";
    public static final String IPADDRESS_PATTERN = "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b"; // ipv4 only

    private RegExp() {}
}
