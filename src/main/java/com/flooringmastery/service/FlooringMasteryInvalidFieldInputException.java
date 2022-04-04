package com.flooringmastery.service;

public class FlooringMasteryInvalidFieldInputException extends Exception {

    public FlooringMasteryInvalidFieldInputException(String message) {
        super(message);
    }

    public FlooringMasteryInvalidFieldInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
