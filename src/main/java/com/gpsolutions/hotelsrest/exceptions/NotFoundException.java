package com.gpsolutions.hotelsrest.exceptions;

public class NotFoundException extends DomainException {

    public NotFoundException(String message) {
        super(message, "not-found");
    }

    public static NotFoundException hotel(String cause) {
        return new NotFoundException("Unknown hotel '%s';".formatted(cause));
    }


}
