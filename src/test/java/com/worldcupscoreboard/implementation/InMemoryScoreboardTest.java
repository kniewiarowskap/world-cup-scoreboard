package com.worldcupscoreboard.implementation;

import com.worldcupscoreboard.exception.InvalidMatchException;
import com.worldcupscoreboard.exception.InvalidMatchStateException;
import com.worldcupscoreboard.exception.MatchNotFoundException;
import com.worldcupscoreboard.exception.TeamAlreadyPlayingException;
import com.worldcupscoreboard.model.MatchResult;
import com.worldcupscoreboard.model.MatchStatus;
import com.worldcupscoreboard.model.MatchSummary;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryScoreboardTest {

    @Test
    void startsMatchAtZeroAndReturnsItInSummary() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        var matchId = board.startMatch("Mexico", "Canada");
        var summary = board.getSummary();
        assertEquals(1, summary.size());
        assertEquals(matchId, summary.getFirst().matchId());
        assertEquals("Mexico", summary.getFirst().homeTeam());
        assertEquals("Canada", summary.getFirst().awayTeam());
        assertEquals(0, summary.getFirst().totalScore());
        assertEquals(MatchStatus.IN_PROGRESS, summary.getFirst().status());
    }

    @Test
    void rejectsInvalidTeamsAndActiveDuplicates() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        assertThrows(InvalidMatchException.class, () -> board.startMatch(null, "Canada"));
        assertThrows(InvalidMatchException.class, () -> board.startMatch("Mexico", null));
        assertThrows(InvalidMatchException.class, () -> board.startMatch(" ", "Canada"));
        assertThrows(InvalidMatchException.class, () -> board.startMatch("Mexico", " mexico "));
        board.startMatch("Mexico", "Canada");
        assertThrows(TeamAlreadyPlayingException.class, () -> board.startMatch("CANADA", "Spain"));
        assertThrows(TeamAlreadyPlayingException.class, () -> board.startMatch("Spain", "MEXICO"));
        assertThrows(TeamAlreadyPlayingException.class, () -> board.startMatch("MEXICO", "CANADA"));
        assertThrows(TeamAlreadyPlayingException.class,
                () -> board.startMatch("\u2003MEXICO\u2003", "Spain"));
    }

    @Test
    void trimsTeamNamesBeforeStoringThem() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        board.startMatch("  Mexico  ", " Canada ");

        var summary = board.getSummary().getFirst();

        assertEquals("Mexico", summary.homeTeam());
        assertEquals("Canada", summary.awayTeam());
    }

    @Test
    void updatesBothSidesAndOrdersByTotalThenLatestStart() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        var mexico = board.startMatch("Mexico", "Canada");
        var spain = board.startMatch("Spain", "Brazil");
        var germany = board.startMatch("Germany", "France");
        updateTo(board, mexico, 0, 5);
        updateTo(board, spain, 2, 2);
        updateTo(board, germany, 2, 2);
        assertEquals(List.of(mexico, germany, spain),
                board.getSummary().stream().map(MatchSummary::matchId).toList());
    }

    @Test
    void finishesMatchMakesTeamsAvailable() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        var matchId = board.startMatch("Mexico", "Canada");
        board.updateScore(matchId, 1, 0);
        board.finishMatch(matchId);
        assertEquals(List.of(), board.getSummary());
        board.startMatch("Canada", "Spain");
        assertThrows(InvalidMatchStateException.class, () -> board.updateScore(matchId, 1, 1));
        assertThrows(InvalidMatchStateException.class, () -> board.finishMatch(matchId));
    }

    @Test
    void permitsScoreCorrectionSuchAsDisallowedGoals() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        var matchId = board.startMatch("A", "B");
        updateTo(board, matchId, 1, 0);
        updateTo(board, matchId, 2, 0);
        updateTo(board, matchId, 2, 1);
        updateTo(board, matchId, 1, 1);
        updateTo(board, matchId, 1, 2);
        assertEquals(1, board.getSummary().getFirst().homeScore());
        assertEquals(2, board.getSummary().getFirst().awayScore());
    }

    @Test
    void rejectsUpdatesThatChangeBothTeamsOrMoreThanOneGoal() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        var matchId = board.startMatch("A", "B");
        assertThrows(InvalidMatchException.class, () -> board.updateScore(matchId, 0, 0));
        assertThrows(InvalidMatchException.class, () -> board.updateScore(matchId, 1, 1));
        assertThrows(InvalidMatchException.class, () -> board.updateScore(matchId, 2, 0));
        board.updateScore(matchId, 1, 0);
        assertThrows(InvalidMatchException.class, () -> board.updateScore(matchId, 0, 1));
    }

    @Test
    void rejectsUnknownMatchesAndInvalidScores() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        var matchId = board.startMatch("A", "B");
        assertThrows(MatchNotFoundException.class, () -> board.updateScore(null, 0, 0));
        assertThrows(MatchNotFoundException.class, () -> board.finishMatch(null));
        assertThrows(IllegalArgumentException.class, () -> board.updateScore(matchId, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> board.updateScore(matchId, 0, -1));
    }

    @Test
    void returnsImmutableSummarySnapshots() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        var matchId = board.startMatch("A", "B");
        var snapshot = board.getSummary();
        board.updateScore(matchId, 1, 0);
        assertEquals(0, snapshot.getFirst().totalScore());
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
    }

    @Test
    void returnsResultForFinishedHomeWin() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        var matchId = board.startMatch("A", "B");
        updateTo(board, matchId, 2, 1);
        board.finishMatch(matchId);

        assertEquals(MatchResult.HOME_WIN, board.getMatchResult(matchId));
    }

    @Test
    void returnsAwayWinAndDrawResults() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        var awayWin = board.startMatch("A", "B");
        updateTo(board, awayWin, 1, 2);
        board.finishMatch(awayWin);

        var draw = board.startMatch("C", "D");
        updateTo(board, draw, 1, 1);
        board.finishMatch(draw);

        assertEquals(MatchResult.AWAY_WIN, board.getMatchResult(awayWin));
        assertEquals(MatchResult.DRAW, board.getMatchResult(draw));
    }

    @Test
    void returnsInProgressForActiveAndRejectsUnknownMatches() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        var matchId = board.startMatch("A", "B");

        assertThrows(MatchNotFoundException.class, () -> board.getMatchResult(null));
        assertEquals(MatchResult.IN_PROGRESS, board.getMatchResult(matchId));
    }

    private static void updateTo(
            InMemoryScoreboard board,
            com.worldcupscoreboard.model.MatchId matchId,
            int homeScore,
            int awayScore) {
        int currentHome = board.getSummary().stream()
                .filter(summary -> summary.matchId().equals(matchId))
                .findFirst().orElseThrow().homeScore();
        int currentAway = board.getSummary().stream()
                .filter(summary -> summary.matchId().equals(matchId))
                .findFirst().orElseThrow().awayScore();
        while (currentHome != homeScore) {
            currentHome += Integer.signum(homeScore - currentHome);
            board.updateScore(matchId, currentHome, currentAway);
        }
        while (currentAway != awayScore) {
            currentAway += Integer.signum(awayScore - currentAway);
            board.updateScore(matchId, currentHome, currentAway);
        }
    }
}
