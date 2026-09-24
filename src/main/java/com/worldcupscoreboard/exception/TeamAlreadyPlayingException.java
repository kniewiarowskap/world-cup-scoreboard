package com.worldcupscoreboard.exception;

/** Indicates that a team is already participating in an active match. */
public class TeamAlreadyPlayingException extends RuntimeException {
    public TeamAlreadyPlayingException(String team) {
        super("Team is already playing: " + team);
    }
}
