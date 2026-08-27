package net.wowdev.ecommerce.orders.service;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(final String message) {
        super(message);
    }
}
