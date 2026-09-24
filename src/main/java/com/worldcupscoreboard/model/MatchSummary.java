package com.worldcupscoreboard.model;

import java.util.Objects;

/**
 * Immutable snapshot of a match exposed by the scoreboard.
 *
 * @param matchId the match identifier
 * @param homeTeam the home team display name
 * @param awayTeam the away team display name
 * @param score the current score
 * @param status the current match lifecycle status
 */
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

    /** @return the current home-team score */
    public int homeScore() {
        return score.home();
    }

    /** @return the current away-team score */
    public int awayScore() {
        return score.away();
    }

    /** @return the combined home and away score */
    public int totalScore() {
        return score.total();
    }
}
