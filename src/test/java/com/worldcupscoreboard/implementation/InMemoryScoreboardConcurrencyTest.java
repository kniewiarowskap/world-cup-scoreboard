package com.worldcupscoreboard.implementation;

import com.worldcupscoreboard.exception.InvalidMatchException;
import com.worldcupscoreboard.exception.InvalidMatchStateException;
import com.worldcupscoreboard.exception.TeamAlreadyPlayingException;
import com.worldcupscoreboard.model.MatchId;
import com.worldcupscoreboard.model.MatchStatus;
import com.worldcupscoreboard.model.MatchSummary;
import com.worldcupscoreboard.model.ScoreChange;
import com.worldcupscoreboard.model.TeamSide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryScoreboardConcurrencyTest {

    private InMemoryScoreboard board;

    @BeforeEach
    void setUp() {
        board = new InMemoryScoreboard();
    }

    @Test
    void startsConcurrentMatchesForDifferentTeams() throws Exception {
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            List<Future<MatchId>> futures = runConcurrently(
                    executor,
                    () -> board.startMatch("Mexico", "Canada"),
                    () -> board.startMatch("Spain", "Brazil")
            );

            MatchId firstMatchId = futures.get(0).get();
            MatchId secondMatchId = futures.get(1).get();

            assertEquals(2, board.getSummary().size());
            assertEquals(
                    Set.of(firstMatchId, secondMatchId),
                    board.getSummary().stream()
                            .map(MatchSummary::matchId)
                            .collect(Collectors.toSet())
            );
        }
    }

    @RepeatedTest(10)
    void allowsOnlyOneConcurrentMatchForAnActiveTeam() {
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            List<Future<MatchId>> futures = runConcurrently(
                    executor,
                    () -> board.startMatch("Mexico", "Canada"),
                    () -> board.startMatch("MEXICO", "Spain")
            );

            assertExactlyOneSucceeds(futures, TeamAlreadyPlayingException.class);
            assertEquals(1, board.getSummary().size());
        }
    }

    @Test
    void serializesConcurrentScoreEvents() throws Exception {
        MatchId matchId = board.startMatch("Mexico", "Canada");
        int updates = 100;

        try (ExecutorService executor = Executors.newFixedThreadPool(8)) {
            List<Callable<Void>> tasks = new ArrayList<>();

            for (int i = 0; i < updates; i++) {
                tasks.add(() -> {
                    board.updateScore(matchId, TeamSide.HOME, ScoreChange.INCREASE);
                    return null;
                });
            }

            invokeAllAndWait(executor, tasks);
        }

        assertEquals(updates, board.getSummary().getFirst().homeScore());
    }

    @Test
    void appliesConcurrentHomeAndAwayScoreEvents() throws Exception {
        MatchId matchId = board.startMatch("Mexico", "Canada");
        int updatesPerSide = 100;

        try (ExecutorService executor = Executors.newFixedThreadPool(8)) {
            List<Callable<Void>> tasks = new ArrayList<>();

            for (int i = 0; i < updatesPerSide; i++) {
                tasks.add(() -> {
                    board.updateScore(matchId, TeamSide.HOME, ScoreChange.INCREASE);
                    return null;
                });
                tasks.add(() -> {
                    board.updateScore(matchId, TeamSide.AWAY, ScoreChange.INCREASE);
                    return null;
                });
            }

            invokeAllAndWait(executor, tasks);
        }

        var match = board.getMatch(matchId);

        assertEquals(updatesPerSide, match.homeScore());
        assertEquals(updatesPerSide, match.awayScore());
    }

    @RepeatedTest(10)
    void rejectsOneOfTwoConcurrentDecreasesWhenOnlyOneGoalIsAvailable() {
        MatchId matchId = board.startMatch("Mexico", "Canada");
        board.updateScore(matchId, TeamSide.HOME, ScoreChange.INCREASE);

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            List<Future<Void>> futures = runConcurrently(
                    executor,
                    () -> {
                        board.updateScore(matchId, TeamSide.HOME, ScoreChange.DECREASE);
                        return null;
                    },
                    () -> {
                        board.updateScore(matchId, TeamSide.HOME, ScoreChange.DECREASE);
                        return null;
                    }
            );

            assertExactlyOneSucceeds(futures, InvalidMatchException.class);
        }

        assertEquals(0, board.getMatch(matchId).homeScore());
    }

    @RepeatedTest(10)
    void allowsOnlyOneConcurrentFinishForAMatch() {
        MatchId matchId = board.startMatch("Mexico", "Canada");

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            List<Future<Void>> futures = runConcurrently(
                    executor,
                    () -> {
                        board.finishMatch(matchId);
                        return null;
                    },
                    () -> {
                        board.finishMatch(matchId);
                        return null;
                    }
            );

            assertExactlyOneSucceeds(futures, InvalidMatchStateException.class);
        }

        assertEquals(MatchStatus.FINISHED, board.getMatch(matchId).status());
        assertTrue(board.getSummary().isEmpty());
        assertDoesNotThrow(() -> board.startMatch("Mexico", "Spain"));
    }

    private static <T> List<Future<T>> runConcurrently(
            ExecutorService executor,
            Callable<T> first,
            Callable<T> second) {

        CountDownLatch start = new CountDownLatch(1);

        Future<T> firstFuture = executor.submit(() -> awaitAndCall(start, first));
        Future<T> secondFuture = executor.submit(() -> awaitAndCall(start, second));

        start.countDown();

        return List.of(firstFuture, secondFuture);
    }

    private static <T> T awaitAndCall(
            CountDownLatch start,
            Callable<T> operation) throws Exception {

        start.await();
        return operation.call();
    }

    private static void invokeAllAndWait(
            ExecutorService executor,
            List<Callable<Void>> tasks) throws Exception {

        for (Future<Void> future : executor.invokeAll(tasks)) {
            future.get();
        }
    }

    private static void assertExactlyOneSucceeds(
            List<? extends Future<?>> futures,
            Class<? extends Throwable> expectedException) {

        assertEquals(2, futures.size());

        int successful = 0;
        int failed = 0;

        for (Future<?> future : futures) {
            try {
                future.get();
                successful++;
            } catch (ExecutionException exception) {
                failed++;
                assertInstanceOf(expectedException, exception.getCause());
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                fail("Test thread was interrupted", exception);
            }
        }

        assertEquals(1, successful, "Exactly one concurrent operation should succeed");
        assertEquals(1, failed, "Exactly one concurrent operation should fail");
    }
}

