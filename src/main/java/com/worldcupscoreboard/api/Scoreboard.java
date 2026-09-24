package com.worldcupscoreboard.api;

import com.worldcupscoreboard.exception.InvalidMatchException;
import com.worldcupscoreboard.exception.InvalidMatchStateException;
import com.worldcupscoreboard.exception.MatchNotFoundException;
import com.worldcupscoreboard.exception.TeamAlreadyPlayingException;
import com.worldcupscoreboard.model.MatchId;
import com.worldcupscoreboard.model.MatchSummary;

import java.util.List;

/**
 * Manages live football matches and their current scores.
 *
 * <p>Implementations must expose only matches that are currently in progress
 * from {@link #getSummary()} and preserve the documented summary ordering.</p>
 */
public interface Scoreboard {
    /**
     * Starts a new match at a score of {@code 0-0}.
     *
     * @param homeTeam the home team name; it must not be blank
     * @param awayTeam the away team name; it must not be blank or equal to the home team
     * @return the generated identifier for the new match
     * @throws InvalidMatchException if either name is invalid
     * @throws TeamAlreadyPlayingException if either team is active
     */
    MatchId startMatch(String homeTeam, String awayTeam);

    /**
     * Replaces the current score with a valid one-goal transition.
     *
     * <p>Exactly one team's score must change by one goal. A one-goal decrease
     * is allowed to represent a correction, such as a disallowed goal.</p>
     *
     * @param matchId the match to update
     * @param homeScore the new home-team score
     * @param awayScore the new away-team score
     * @throws MatchNotFoundException if the match is unknown
     * @throws InvalidMatchStateException if the match is finished
     * @throws InvalidMatchException if the transition is invalid
     * @throws IllegalArgumentException if either score is negative
     */
    void updateScore(MatchId matchId, int homeScore, int awayScore);

    /**
     * Finishes a match and releases both teams for future matches.
     *
     * @param matchId the match to finish
     * @throws MatchNotFoundException if the match is unknown
     * @throws InvalidMatchStateException if the match is already finished
     */
    void finishMatch(MatchId matchId);

    /**
     * Returns an immutable snapshot of all matches currently in progress.
     *
     * <p>Results are ordered by total score descending, then by start time
     * descending when scores are tied.</p>
     *
     * @return an immutable ordered snapshot of active match summaries
     */
    List<MatchSummary> getSummary();
}
