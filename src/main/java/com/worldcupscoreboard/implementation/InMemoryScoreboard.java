package com.worldcupscoreboard.implementation;

import com.worldcupscoreboard.api.Scoreboard;
import com.worldcupscoreboard.exception.InvalidMatchException;
import com.worldcupscoreboard.exception.MatchNotFoundException;
import com.worldcupscoreboard.exception.TeamAlreadyPlayingException;
import com.worldcupscoreboard.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe in-memory implementation of the scoreboard API.
 */
public final class InMemoryScoreboard implements Scoreboard {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private final Map<MatchId, Match> matches = new HashMap<>();
    private final Map<Team, MatchId> activeTeams = new HashMap<>();
    private long nextStartSequence;

    @Override
    public MatchId startMatch(String homeTeam, String awayTeam) {
        Team home = new Team(homeTeam);
        Team away = new Team(awayTeam);

        if (home.equals(away)) {
            throw new InvalidMatchException("Home and away teams must be different");
        }

        writeLock.lock();
        try {
            ensureTeamsAvailable(home, away);
            Match match = new Match(home, away, nextStartSequence++);
            MatchId matchId = match.matchId;
            matches.put(matchId, match);
            activeTeams.put(home, matchId);
            activeTeams.put(away, matchId);
            return matchId;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void updateScore(MatchId matchId, TeamSide teamSide, ScoreChange change) {
        writeLock.lock();
        try {
            Match match = requireMatch(matchId);
            match.updateScore(teamSide, change);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void finishMatch(MatchId matchId) {
        writeLock.lock();
        try {
            Match match = requireMatch(matchId);
            match.finish();
            activeTeams.remove(match.homeTeam);
            activeTeams.remove(match.awayTeam);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public MatchSummary getMatch(MatchId matchId) {
        readLock.lock();
        try {
            return requireMatch(matchId).summary();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<MatchSummary> getSummary() {
        return snapshotActiveMatches().stream()
                .sorted(InMemoryScoreboard::compareSnapshots)
                .map(SummarySnapshot::summary)
                .toList();
    }

    private List<SummarySnapshot> snapshotActiveMatches() {
        List<SummarySnapshot> snapshot;
        readLock.lock();
        try {
            snapshot = matches.values().stream()
                    .filter(match -> match.status == MatchStatus.IN_PROGRESS)
                    .map(match -> new SummarySnapshot(match.summary(), match.startSequence))
                    .toList();
        } finally {
            readLock.unlock();
        }
        return snapshot;
    }

    private static int compareSnapshots(SummarySnapshot first, SummarySnapshot second) {
        int scoreOrder = Integer.compare(
                second.summary().totalScore(),
                first.summary().totalScore());
        if (scoreOrder != 0) {
            return scoreOrder;
        }
        return Long.compare(second.startSequence(), first.startSequence());
    }

    private void ensureTeamsAvailable(Team team1, Team team2) {
        if (activeTeams.containsKey(team1)) {
            throw new TeamAlreadyPlayingException(team1.name());
        }
        if (activeTeams.containsKey(team2)) {
            throw new TeamAlreadyPlayingException(team2.name());
        }
    }

    private Match requireMatch(MatchId matchId) {
        if (matchId == null || !matches.containsKey(matchId)) {
            throw new MatchNotFoundException(matchId);
        }
        return matches.get(matchId);
    }

    private record SummarySnapshot(MatchSummary summary, long startSequence) {
    }
}
