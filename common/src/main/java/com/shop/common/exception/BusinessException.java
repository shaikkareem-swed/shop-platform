package com.shop.common.exception;

/** Business rule violation -> HTTP 409. */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
