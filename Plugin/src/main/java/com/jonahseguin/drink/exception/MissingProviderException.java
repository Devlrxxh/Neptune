package com.jonahseguin.drink.exception;

public class MissingProviderException extends Exception {

    public MissingProviderException(String message) {
        super(message);
    }

    public MissingProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
