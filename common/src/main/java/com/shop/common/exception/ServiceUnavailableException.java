package com.shop.common.exception;

/** A downstream dependency is down / circuit is open -> HTTP 503. */
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
