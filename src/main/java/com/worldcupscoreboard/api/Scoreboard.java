package com.worldcupscoreboard.api;

import com.worldcupscoreboard.exception.InvalidMatchException;
import com.worldcupscoreboard.exception.InvalidMatchStateException;
import com.worldcupscoreboard.exception.MatchNotFoundException;
import com.worldcupscoreboard.exception.TeamAlreadyPlayingException;
import com.worldcupscoreboard.model.MatchId;
import com.worldcupscoreboard.model.MatchSummary;
import com.worldcupscoreboard.model.ScoreChange;
import com.worldcupscoreboard.model.TeamSide;

import java.util.List;

/**
 * Manages live football matches and their current scores.
 */
public interface Scoreboard {
    /**
     * Starts a new match at a score of {@code 0-0}.
     *
     * @param homeTeam the home country name; it must contain only English
     *        letters and spaces and must not be blank
     * @param awayTeam the away country name; it must contain only English
     *        letters and spaces, must not be blank, and must differ from the home team
     * @return the generated identifier for the new match
     * @throws InvalidMatchException if either name is invalid
     * @throws TeamAlreadyPlayingException if either team is active
     */
    MatchId startMatch(String homeTeam, String awayTeam);

    /**
     * Applies one score-change event to the selected team.
     *
     * <p>An increase records a goal; a decrease represents a correction, such
     * as a disallowed goal. A team's score cannot be decreased below zero.</p>
     *
     * @param matchId the match to update
     * @param teamSide the side whose score changes
     * @param change whether to increase or decrease that side's score
     * @throws MatchNotFoundException if the match is unknown
     * @throws InvalidMatchStateException if the match is finished
     * @throws InvalidMatchException if either event argument is null or the
     *         change would make the score negative or exceed its supported range
     */
    void updateScore(MatchId matchId, TeamSide teamSide, ScoreChange change);

    /**
     * Finishes a match and releases both teams for future matches.
     *
     * @param matchId the match to finish
     * @throws MatchNotFoundException if the match is unknown
     * @throws InvalidMatchStateException if the match is already finished
     */
    void finishMatch(MatchId matchId);

    /**
     * Returns an immutable snapshot of a specific match.
     *
     * <p>The snapshot can represent either an active or finished match and
     * includes its identifier, teams, score, and lifecycle status.</p>
     *
     * @param matchId the match to retrieve
     * @return the requested match snapshot
     * @throws MatchNotFoundException if the match is unknown
     */
    MatchSummary getMatch(MatchId matchId);

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
