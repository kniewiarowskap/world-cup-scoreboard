package com.worldcupscoreboard.exception;

/** Indicates invalid match input or an invalid score transition. */
public class InvalidMatchException extends RuntimeException {
    public InvalidMatchException(String message) {
        super(message);
    }
}
