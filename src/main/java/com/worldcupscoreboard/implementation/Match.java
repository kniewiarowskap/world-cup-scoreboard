package com.worldcupscoreboard.implementation;

import com.worldcupscoreboard.exception.InvalidMatchException;
import com.worldcupscoreboard.exception.InvalidMatchStateException;
import com.worldcupscoreboard.model.MatchId;
import com.worldcupscoreboard.model.MatchStatus;
import com.worldcupscoreboard.model.MatchSummary;
import com.worldcupscoreboard.model.Score;
import com.worldcupscoreboard.model.ScoreChange;
import com.worldcupscoreboard.model.Team;
import com.worldcupscoreboard.model.TeamSide;

final class Match {

    final MatchId matchId;
    final Team homeTeam;
    final Team awayTeam;
    final long startSequence;
    Score score;
    MatchStatus status = MatchStatus.IN_PROGRESS;

    Match(Team homeTeam, Team awayTeam, long startSequence) {
        this.matchId = MatchId.generate();
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.startSequence = startSequence;
        this.score = new Score(0, 0);
    }

    MatchSummary summary() {
        return new MatchSummary(matchId, homeTeam.name(), awayTeam.name(), score, status);
    }

    void updateScore(TeamSide teamSide, ScoreChange change) {
        ensureInProgress();
        if (teamSide == null || change == null) {
            throw new InvalidMatchException("Team side and score change must not be null");
        }

        int delta = change.delta();
        score = switch (teamSide) {
            case HOME -> new Score(applyChange(score.home(), delta), score.away());
            case AWAY -> new Score(score.home(), applyChange(score.away(), delta));
        };
    }

    void finish() {
        ensureInProgress();
        status = MatchStatus.FINISHED;
    }

    private void ensureInProgress() {
        if (status != MatchStatus.IN_PROGRESS) {
            throw new InvalidMatchStateException("Match is already finished: " + matchId);
        }
    }

    private static int applyChange(int currentScore, int delta) {
        if ((delta < 0 && currentScore == 0) || (delta > 0 && currentScore == Integer.MAX_VALUE)) {
            throw new InvalidMatchException("Score change is outside the supported range");
        }
        return currentScore + delta;
    }
}
