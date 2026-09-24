package com.worldcupscoreboard.exception;

import com.worldcupscoreboard.model.MatchId;

/** Indicates that an operation referenced an unknown match. */
public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(MatchId matchId) {
        super("Match not found: " + matchId);
    }
}
