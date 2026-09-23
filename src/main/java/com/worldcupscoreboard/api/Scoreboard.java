package com.worldcupscoreboard.api;

import com.worldcupscoreboard.model.MatchId;
import com.worldcupscoreboard.model.MatchSummary;

import java.util.List;

public interface Scoreboard {
    MatchId startMatch(String homeTeam, String awayTeam);

    void updateScore(MatchId matchId, int homeScore, int awayScore);

    void finishMatch(MatchId matchId);

    List<MatchSummary> getSummary();
}
