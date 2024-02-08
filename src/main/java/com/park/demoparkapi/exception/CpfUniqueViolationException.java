package com.park.demoparkapi.exception;

public class CpfUniqueViolationException extends RuntimeException {
    public CpfUniqueViolationException(String msg) {
        super(msg);
    }
}
