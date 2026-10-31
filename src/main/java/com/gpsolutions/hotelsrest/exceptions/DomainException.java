package com.gpsolutions.hotelsrest.exceptions;

public abstract class DomainException extends RuntimeException {

    private final String typeSuffix;

    protected DomainException(String message, String typeSuffix) {
        super(message);
        this.typeSuffix = typeSuffix;
    }

    protected DomainException(String message, String typeSuffix, Throwable cause) {
        super(message, cause);
        this.typeSuffix = typeSuffix;
    }

    public String typeSuffix() {
        return this.typeSuffix;
    }

}
