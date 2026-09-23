package com.worldcupscoreboard.exception;

import com.worldcupscoreboard.model.MatchId;

public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(MatchId matchId) {
        super("Match not found: " + matchId);
    }
}
