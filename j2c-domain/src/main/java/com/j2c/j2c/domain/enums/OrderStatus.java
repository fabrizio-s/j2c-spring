package com.j2c.j2c.domain.enums;

public enum OrderStatus {
    CREATED,
    CONFIRMED,
    PROCESSING,
    PARTIALLY_FULFILLED,
    FULFILLED,
    CANCELLED;

    public boolean isFinalizing() {
        return OrderStatus.CANCELLED.equals(this) || OrderStatus.FULFILLED.equals(this);
    }

}
