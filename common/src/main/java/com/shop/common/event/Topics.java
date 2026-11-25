package com.shop.common.event;

public final class Topics {
    public static final String ORDER_CREATED = "order-created";
    public static final String INVENTORY_RESERVED = "inventory-reserved";
    public static final String INVENTORY_REJECTED = "inventory-rejected";
    /** Order state changes -> consumed by the CQRS read-model projection. */
    public static final String ORDER_EVENTS = "order-events";

    private Topics() {}
}
