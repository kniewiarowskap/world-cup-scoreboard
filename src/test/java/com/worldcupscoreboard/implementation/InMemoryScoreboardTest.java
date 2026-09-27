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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryScoreboardTest {

    private InMemoryScoreboard board;

    @BeforeEach
    void setUp() {
        board = new InMemoryScoreboard();
    }

    @Test
    void startsMatchAtZeroAndReturnsItInSummary() {
        var matchId = board.startMatch("Mexico", "Canada");
        var summary = board.getSummary().getFirst();

        assertEquals(1, board.getSummary().size());
        assertEquals(matchId, summary.matchId());
        assertEquals("MEXICO", summary.homeTeam());
        assertEquals("CANADA", summary.awayTeam());
        assertEquals(0, summary.totalScore());
        assertEquals(MatchStatus.IN_PROGRESS, summary.status());
    }

    @ParameterizedTest
    @MethodSource("invalidTeamNames")
    void rejectsInvalidTeamNames(String homeTeam, String awayTeam, String expectedMessage) {
        InvalidMatchException exception = assertThrows(
                InvalidMatchException.class,
                () -> board.startMatch(homeTeam, awayTeam)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Arguments> invalidTeamNames() {
        return Stream.of(
                Arguments.of(null, "Canada", "Team name must not be blank"),
                Arguments.of("Mexico", null, "Team name must not be blank"),
                Arguments.of(" ", "Canada", "Team name may contain only English letters and spaces"),
                Arguments.of("Congo-DR", "Spain", "Team name may contain only English letters and spaces")
        );
    }

    @ParameterizedTest
    @MethodSource("sameTeamNamePairs")
    void rejectsMatchBetweenSameTeams(String homeTeam, String awayTeam) {
        InvalidMatchException exception = assertThrows(
                InvalidMatchException.class,
                () -> board.startMatch(homeTeam, awayTeam)
        );

        assertEquals("Home and away teams must be different", exception.getMessage());
    }

    private static Stream<Arguments> sameTeamNamePairs() {
        return Stream.of(
                Arguments.of("Mexico", "Mexico"),
                Arguments.of("Mexico", "MEXICO"),
                Arguments.of("  Mexico ", "\u2003MEXICO\u2003")
        );
    }

    @ParameterizedTest
    @MethodSource("activeTeamConflicts")
    void rejectsStartingAnotherMatchWithAnActiveTeam(String homeTeam, String awayTeam) {
        board.startMatch("Mexico", "Canada");

        assertThrows(
                TeamAlreadyPlayingException.class,
                () -> board.startMatch(homeTeam, awayTeam)
        );
    }

    private static Stream<Arguments> activeTeamConflicts() {
        return Stream.of(
                Arguments.of("CANADA", "Spain"),
                Arguments.of("Spain", "MEXICO"),
                Arguments.of("MEXICO", "CANADA"),
                Arguments.of("\u2003MEXICO\u2003", "Spain")
        );
    }

    @ParameterizedTest
    @MethodSource("teamNamePairsWithWhitespace")
    void normalizesTeamNamesBeforeStoringThem(
            String homeTeam,
            String awayTeam,
            String expectedHomeTeam,
            String expectedAwayTeam) {

        board.startMatch(homeTeam, awayTeam);
        var summary = board.getSummary().getFirst();

        assertEquals(expectedHomeTeam, summary.homeTeam());
        assertEquals(expectedAwayTeam, summary.awayTeam());
    }

    private static Stream<Arguments> teamNamePairsWithWhitespace() {
        return Stream.of(
                Arguments.of("  Mexico  ", " Canada ", "MEXICO", "CANADA"),
                Arguments.of("United   States", " Costa   Rica ", "UNITED STATES", "COSTA RICA"),
                Arguments.of("\u2003Japan\u2003", "\tSouth\tKorea\t", "JAPAN", "SOUTH KOREA")
        );
    }

    @Test
    void ordersMatchesByTotalScoreAndThenLatestStart() {
        var mexico = board.startMatch("Mexico", "Canada");
        var spain = board.startMatch("Spain", "Brazil");
        var germany = board.startMatch("Germany", "France");

        score(mexico, TeamSide.AWAY, 5);
        score(spain, TeamSide.HOME, 2);
        score(spain, TeamSide.AWAY, 2);
        score(germany, TeamSide.HOME, 2);
        score(germany, TeamSide.AWAY, 2);

        assertEquals(
                List.of(mexico, germany, spain),
                board.getSummary().stream().map(MatchSummary::matchId).toList()
        );
    }

    @Test
    void ordersExampleScenarioByTotalThenLatestStart() {
        var mexico = board.startMatch("Mexico", "Canada");
        var spain = board.startMatch("Spain", "Brazil");
        var germany = board.startMatch("Germany", "France");
        var uruguay = board.startMatch("Uruguay", "Italy");
        var argentina = board.startMatch("Argentina", "Australia");

        score(mexico, TeamSide.AWAY, 5);
        score(spain, TeamSide.HOME, 10);
        score(spain, TeamSide.AWAY, 2);
        score(germany, TeamSide.HOME, 2);
        score(germany, TeamSide.AWAY, 2);
        score(uruguay, TeamSide.HOME, 6);
        score(uruguay, TeamSide.AWAY, 6);
        score(argentina, TeamSide.HOME, 3);
        score(argentina, TeamSide.AWAY, 1);

        assertEquals(
                List.of(
                        "URUGUAY 6 - ITALY 6",
                        "SPAIN 10 - BRAZIL 2",
                        "MEXICO 0 - CANADA 5",
                        "ARGENTINA 3 - AUSTRALIA 1",
                        "GERMANY 2 - FRANCE 2"
                ),
                board.getSummary().stream()
                        .map(match -> "%s %d - %s %d".formatted(
                                match.homeTeam(),
                                match.homeScore(),
                                match.awayTeam(),
                                match.awayScore()
                        ))
                        .toList()
        );
    }

    @Test
    void finishesMatchAndMakesTeamsAvailable() {
        var matchId = board.startMatch("Mexico", "Canada");
        score(matchId, TeamSide.HOME, 1);

        board.finishMatch(matchId);

        assertTrue(board.getSummary().isEmpty());

        board.startMatch("Canada", "Spain");

        assertThrows(
                InvalidMatchStateException.class,
                () -> board.updateScore(matchId, TeamSide.AWAY, ScoreChange.INCREASE)
        );
        assertThrows(
                InvalidMatchStateException.class,
                () -> board.finishMatch(matchId)
        );
    }

    @Test
    void permitsScoreCorrectionSuchAsDisallowedGoals() {
        var matchId = board.startMatch("A", "B");

        score(matchId, TeamSide.HOME, 2);
        score(matchId, TeamSide.AWAY, 1);
        board.updateScore(matchId, TeamSide.HOME, ScoreChange.DECREASE);
        board.updateScore(matchId, TeamSide.AWAY, ScoreChange.INCREASE);

        var match = board.getSummary().getFirst();

        assertEquals(1, match.homeScore());
        assertEquals(2, match.awayScore());
    }

    @Test
    void rejectsInvalidScoreChangeEvents() {
        var matchId = board.startMatch("A", "B");

        assertThrows(
                InvalidMatchException.class,
                () -> board.updateScore(matchId, TeamSide.HOME, ScoreChange.DECREASE)
        );
        assertThrows(
                InvalidMatchException.class,
                () -> board.updateScore(matchId, null, ScoreChange.INCREASE)
        );
        assertThrows(
                InvalidMatchException.class,
                () -> board.updateScore(matchId, TeamSide.AWAY, null)
        );
    }

    @ParameterizedTest
    @EnumSource(TeamSide.class)
    void doesNotAllowScoreToFallBelowZero(TeamSide teamSide) {
        var matchId = board.startMatch("A", "B");

        score(matchId, teamSide, 1);
        board.updateScore(matchId, teamSide, ScoreChange.DECREASE);

        assertThrows(
                InvalidMatchException.class,
                () -> board.updateScore(matchId, teamSide, ScoreChange.DECREASE)
        );

        MatchSummary match = board.getMatch(matchId);

        int score = switch (teamSide) {
            case HOME -> match.homeScore();
            case AWAY -> match.awayScore();
        };

        assertEquals(0, score);
    }

    @Test
    void rejectsUnknownMatchesForScoreUpdates() {
        board.startMatch("A", "B");

        assertThrows(
                MatchNotFoundException.class,
                () -> board.updateScore(null, TeamSide.HOME, ScoreChange.INCREASE)
        );
        assertThrows(
                MatchNotFoundException.class,
                () -> board.finishMatch(null)
        );
    }

    @Test
    void returnsImmutableSummarySnapshots() {
        var matchId = board.startMatch("A", "B");
        var snapshot = board.getSummary();

        score(matchId, TeamSide.HOME, 1);

        assertEquals(0, snapshot.getFirst().totalScore());
        assertThrows(
                UnsupportedOperationException.class,
                snapshot::clear
        );
    }

    @Test
    void retrievesFinishedMatchSnapshot() {
        var matchId = board.startMatch("A", "B");

        score(matchId, TeamSide.HOME, 2);
        score(matchId, TeamSide.AWAY, 1);
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
        var matchId = board.startMatch("A", "B");
        score(matchId, TeamSide.HOME, 1);

        var match = board.getMatch(matchId);

        assertEquals(1, match.homeScore());
        assertEquals(0, match.awayScore());
        assertEquals(MatchStatus.IN_PROGRESS, match.status());
    }

    @ParameterizedTest
    @MethodSource("unknownMatchIds")
    void rejectsUnknownMatchRetrieval(MatchId matchId) {
        assertThrows(
                MatchNotFoundException.class,
                () -> board.getMatch(matchId)
        );
    }

    private static Stream<Arguments> unknownMatchIds() {
        return Stream.of(
                Arguments.of((MatchId) null),
                Arguments.of(MatchId.generate())
        );
    }


    private void score(MatchId matchId, TeamSide teamSide, int goals) {
        for (int i = 0; i < goals; i++) {
            board.updateScore(matchId, teamSide, ScoreChange.INCREASE);
        }
    }
}

