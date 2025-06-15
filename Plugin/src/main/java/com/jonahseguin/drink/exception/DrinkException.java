package com.jonahseguin.drink.exception;

public class DrinkException extends RuntimeException {

    public DrinkException() {
    }

    public DrinkException(String message) {
        super(message);
    }

    public DrinkException(String message, Throwable cause) {
        super(message, cause);
    }

    public DrinkException(Throwable cause) {
        super(cause);
    }
}
