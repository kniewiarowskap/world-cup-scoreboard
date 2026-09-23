package com.worldcupscoreboard.exception;

/** Indicates that an operation is invalid for the match lifecycle state. */
public class InvalidMatchStateException extends RuntimeException {
    public InvalidMatchStateException(String message) {
        super(message);
    }
}
