package com.worldcupscoreboard.implementation;

import com.worldcupscoreboard.exception.InvalidMatchException;
import com.worldcupscoreboard.exception.TeamAlreadyPlayingException;
import com.worldcupscoreboard.model.MatchId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class InMemoryScoreboardConcurrencyTest {
    @Test
    void allowsOnlyOneConcurrentMatchForAnActiveTeam() throws Exception {
        InMemoryScoreboard board = new InMemoryScoreboard();
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Callable<MatchId> first = () -> board.startMatch("Mexico", "Canada");
            Callable<MatchId> second = () -> board.startMatch("MEXICO", "Spain");
            List<Future<MatchId>> results = executor.invokeAll(List.of(first, second));
            long successes = results.stream().filter(this::completedSuccessfully).count();
            long conflicts = results.stream().filter(this::completedWithTeamConflict).count();
            assertEquals(1, successes);
            assertEquals(1, conflicts);
            assertEquals(1, board.getSummary().size());
        }
    }

    @Test
    void serializesConcurrentScoreUpdatesWithoutAcceptingDuplicateEvents() throws Exception {
        InMemoryScoreboard board = new InMemoryScoreboard();
        MatchId matchId = board.startMatch("Mexico", "Canada");
        int updates = 100;
        List<Future<Void>> results;
        try (ExecutorService executor = Executors.newFixedThreadPool(8)) {
            List<Callable<Void>> tasks = new ArrayList<>();
            for (int i = 0; i < updates; i++) {
                tasks.add(() -> {
                    board.updateScore(matchId, 1, 0);
                    return null;
                });
            }
            results = executor.invokeAll(tasks);
        }
        long successfulUpdates = 0;
        for (Future<Void> result : results) {
            if (completedScoreUpdate(result)) {
                successfulUpdates++;
            }
        }
        assertEquals(1, successfulUpdates);
        assertEquals(1, board.getSummary().getFirst().homeScore());
    }

    private boolean completedScoreUpdate(Future<Void> result) {
        try {
            result.get(1, TimeUnit.SECONDS);
            return true;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Test interrupted", exception);
        } catch (ExecutionException exception) {
            assertInstanceOf(InvalidMatchException.class, exception.getCause());
            return false;
        } catch (TimeoutException exception) {
            return false;
        }
    }

    private boolean completedSuccessfully(Future<MatchId> result) {
        try {
            result.get(1, TimeUnit.SECONDS);
            return true;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Test interrupted", exception);
        } catch (ExecutionException | TimeoutException exception) {
            return false;
        }
    }

    private boolean completedWithTeamConflict(Future<MatchId> result) {
        try {
            result.get(1, TimeUnit.SECONDS);
            return false;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Test interrupted", exception);
        } catch (ExecutionException exception) {
            assertInstanceOf(TeamAlreadyPlayingException.class, exception.getCause());
            return true;
        } catch (TimeoutException exception) {
            return false;
        }
    }
}
