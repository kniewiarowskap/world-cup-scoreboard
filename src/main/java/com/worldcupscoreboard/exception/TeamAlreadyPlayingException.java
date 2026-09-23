package com.worldcupscoreboard.exception;

public class TeamAlreadyPlayingException extends RuntimeException {
    public TeamAlreadyPlayingException(String team) {
        super("Team is already playing: " + team);
    }
}
