package com.worldcupscoreboard.implementation;

import com.worldcupscoreboard.exception.InvalidMatchStateException;
import com.worldcupscoreboard.model.MatchId;
import com.worldcupscoreboard.model.MatchResult;
import com.worldcupscoreboard.model.MatchStatus;
import com.worldcupscoreboard.model.MatchSummary;
import com.worldcupscoreboard.model.Score;

final class MatchState {
    final MatchId matchId;
    final String homeTeam;
    final String awayTeam;
    final long startSequence;
    Score score;
    MatchStatus status = MatchStatus.IN_PROGRESS;

    MatchState(
            MatchId matchId,
            String homeTeam,
            String awayTeam,
            long startSequence,
            Score score) {
        this.matchId = matchId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.startSequence = startSequence;
        this.score = score;
    }

    MatchSummary summary() {
        return new MatchSummary(matchId, homeTeam, awayTeam, score, status);
    }

    void updateScore(Score updatedScore) {
        ensureInProgress();
        ScoreUpdateValidator.validate(score, updatedScore);
        score = updatedScore;
    }

    void finish() {
        ensureInProgress();
        status = MatchStatus.FINISHED;
    }

    MatchResult getResult() {
        if (status != MatchStatus.FINISHED) {
            return MatchResult.IN_PROGRESS;
        }
        if (score.home() > score.away()) {
            return MatchResult.HOME_WIN;
        }
        if (score.home() < score.away()) {
            return MatchResult.AWAY_WIN;
        }
        return MatchResult.DRAW;
    }

    private void ensureInProgress() {
        if (status != MatchStatus.IN_PROGRESS) {
            throw new InvalidMatchStateException("Match is already finished: " + matchId);
        }
    }
}
