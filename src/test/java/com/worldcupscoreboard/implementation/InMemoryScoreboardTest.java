package com.worldcupscoreboard.implementation;

import com.worldcupscoreboard.exception.InvalidMatchException;
import com.worldcupscoreboard.exception.InvalidMatchStateException;
import com.worldcupscoreboard.exception.MatchNotFoundException;
import com.worldcupscoreboard.exception.TeamAlreadyPlayingException;
import com.worldcupscoreboard.model.MatchId;
import com.worldcupscoreboard.model.MatchStatus;
import com.worldcupscoreboard.model.MatchSummary;
import com.worldcupscoreboard.model.ScoreChange;
import com.worldcupscoreboard.model.TeamSide;
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
        assertEquals("MEXICO", summary.getFirst().homeTeam());
        assertEquals("CANADA", summary.getFirst().awayTeam());
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
        assertThrows(InvalidMatchException.class,
                () -> board.startMatch("Congo-DR", "Spain"));
    }

    @Test
    void trimsTeamNamesBeforeStoringThem() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        board.startMatch("  Mexico  ", " Canada ");

        var summary = board.getSummary().getFirst();

        assertEquals("MEXICO", summary.homeTeam());
        assertEquals("CANADA", summary.awayTeam());
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
        board.updateScore(matchId, TeamSide.HOME, ScoreChange.INCREASE);
        board.finishMatch(matchId);
        assertEquals(List.of(), board.getSummary());
        board.startMatch("Canada", "Spain");
        assertThrows(InvalidMatchStateException.class,
                () -> board.updateScore(matchId, TeamSide.AWAY, ScoreChange.INCREASE));
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
    void rejectsInvalidScoreChangeEvents() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        var matchId = board.startMatch("A", "B");
        assertThrows(InvalidMatchException.class,
                () -> board.updateScore(matchId, TeamSide.HOME, ScoreChange.DECREASE));
        assertThrows(InvalidMatchException.class,
                () -> board.updateScore(matchId, null, ScoreChange.INCREASE));
        assertThrows(InvalidMatchException.class,
                () -> board.updateScore(matchId, TeamSide.AWAY, null));
    }

    @Test
    void doesNotAllowScoreToFallBelowZero() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        var matchId = board.startMatch("A", "B");

        board.updateScore(matchId, TeamSide.HOME, ScoreChange.INCREASE);
        board.updateScore(matchId, TeamSide.HOME, ScoreChange.DECREASE);

        assertEquals(0, board.getMatch(matchId).homeScore());
        assertThrows(InvalidMatchException.class,
                () -> board.updateScore(matchId, TeamSide.HOME, ScoreChange.DECREASE));
        assertEquals(0, board.getMatch(matchId).homeScore());
    }

    @Test
    void rejectsUnknownMatchesForScoreUpdates() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        var matchId = board.startMatch("A", "B");
        assertThrows(MatchNotFoundException.class,
                () -> board.updateScore(null, TeamSide.HOME, ScoreChange.INCREASE));
        assertThrows(MatchNotFoundException.class, () -> board.finishMatch(null));
    }

    @Test
    void returnsImmutableSummarySnapshots() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        var matchId = board.startMatch("A", "B");
        var snapshot = board.getSummary();
        board.updateScore(matchId, TeamSide.HOME, ScoreChange.INCREASE);
        assertEquals(0, snapshot.getFirst().totalScore());
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
    }

    @Test
    void retrievesFinishedMatchSnapshot() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        var matchId = board.startMatch("A", "B");
        updateTo(board, matchId, 2, 1);
        board.finishMatch(matchId);

        var match = board.getMatch(matchId);
        assertEquals(matchId, match.matchId());
        assertEquals("A", match.homeTeam());
        assertEquals("B", match.awayTeam());
        assertEquals(2, match.homeScore());
        assertEquals(1, match.awayScore());
        assertEquals(MatchStatus.FINISHED, match.status());
    }

    @Test
    void retrievesActiveMatchSnapshot() {
        InMemoryScoreboard board = new InMemoryScoreboard();
        var matchId = board.startMatch("A", "B");
        board.updateScore(matchId, TeamSide.HOME, ScoreChange.INCREASE);

        var match = board.getMatch(matchId);
        assertEquals(1, match.homeScore());
        assertEquals(0, match.awayScore());
        assertEquals(MatchStatus.IN_PROGRESS, match.status());
    }

    @Test
    void rejectsUnknownMatchRetrieval() {
        InMemoryScoreboard board = new InMemoryScoreboard();

        assertThrows(MatchNotFoundException.class, () -> board.getMatch(null));
        assertThrows(MatchNotFoundException.class, () -> board.getMatch(MatchId.generate()));
    }

    private static void updateTo(
            InMemoryScoreboard board,
            com.worldcupscoreboard.model.MatchId matchId,
            int homeScore,
            int awayScore) {
        MatchSummary current = board.getMatch(matchId);
        int currentHome = current.homeScore();
        int currentAway = current.awayScore();
        while (currentHome != homeScore) {
            ScoreChange change = homeScore > currentHome
                    ? ScoreChange.INCREASE
                    : ScoreChange.DECREASE;
            board.updateScore(matchId, TeamSide.HOME, change);
            currentHome += Integer.signum(homeScore - currentHome);
        }
        while (currentAway != awayScore) {
            ScoreChange change = awayScore > currentAway
                    ? ScoreChange.INCREASE
                    : ScoreChange.DECREASE;
            board.updateScore(matchId, TeamSide.AWAY, change);
            currentAway += Integer.signum(awayScore - currentAway);
        }
    }
}
