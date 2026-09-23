package com.worldcupscoreboard.model;

import java.util.Objects;

public record MatchSummary(
        MatchId matchId,
        String homeTeam,
        String awayTeam,
        Score score,
        MatchStatus status) {
    public MatchSummary {
        Objects.requireNonNull(matchId, "matchId must not be null");
        Objects.requireNonNull(homeTeam, "homeTeam must not be null");
        Objects.requireNonNull(awayTeam, "awayTeam must not be null");
        Objects.requireNonNull(score, "score must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }

    public int homeScore() {
        return score.home();
    }

    public int awayScore() {
        return score.away();
    }

    public int totalScore() {
        return score.total();
    }
}
