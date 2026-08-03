package com.siteoperationsservice.exceptions;

public class GeofenceViolationException extends RuntimeException {
    public GeofenceViolationException(String message) {
        super(message);
    }
}
