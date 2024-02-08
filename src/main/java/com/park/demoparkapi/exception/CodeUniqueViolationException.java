package com.park.demoparkapi.exception;

public class CodeUniqueViolationException extends RuntimeException {
    public CodeUniqueViolationException(String msg) {
        super(msg);
    }
}
