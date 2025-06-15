package com.jonahseguin.drink.exception;

public class CommandArgumentException extends Exception {

    public CommandArgumentException() {
    }

    public CommandArgumentException(String message) {
        super(message);
    }

    public CommandArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandArgumentException(Throwable cause) {
        super(cause);
    }

}
