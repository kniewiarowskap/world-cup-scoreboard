package com.worldcupscoreboard.implementation;

import com.worldcupscoreboard.api.Scoreboard;
import com.worldcupscoreboard.exception.InvalidMatchException;
import com.worldcupscoreboard.exception.MatchNotFoundException;
import com.worldcupscoreboard.exception.TeamAlreadyPlayingException;
import com.worldcupscoreboard.model.MatchId;
import com.worldcupscoreboard.model.MatchStatus;
import com.worldcupscoreboard.model.MatchSummary;
import com.worldcupscoreboard.model.Score;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class InMemoryScoreboard implements Scoreboard {
    private final Object lock = new Object();
    private final Map<MatchId, MatchState> matches = new HashMap<>();
    private final Map<String, MatchId> activeTeams = new HashMap<>();
    private long nextStartSequence;

    @Override
    public MatchId startMatch(String homeTeam, String awayTeam) {
        String home = validateTeam(homeTeam);
        String away = validateTeam(awayTeam);
        String normalizedHome = normalize(home);
        String normalizedAway = normalize(away);
        if (normalizedHome.equals(normalizedAway)) {
            throw new InvalidMatchException("Home and away teams must be different");
        }

        synchronized (lock) {
            ensureTeamAvailable(home, normalizedHome);
            ensureTeamAvailable(away, normalizedAway);
            MatchId matchId = MatchId.generate();
            MatchState match = new MatchState(
                    matchId, home, away, nextStartSequence++, new Score(0, 0));
            matches.put(matchId, match);
            activeTeams.put(normalizedHome, matchId);
            activeTeams.put(normalizedAway, matchId);
            return matchId;
        }
    }

    @Override
    public void updateScore(MatchId matchId, int homeScore, int awayScore) {
        synchronized (lock) {
            MatchState match = requireMatch(matchId);
            match.updateScore(new Score(homeScore, awayScore));
        }
    }

    @Override
    public void finishMatch(MatchId matchId) {
        synchronized (lock) {
            MatchState match = requireMatch(matchId);
            match.finish();
            activeTeams.remove(normalize(match.homeTeam));
            activeTeams.remove(normalize(match.awayTeam));
        }
    }

    @Override
    public List<MatchSummary> getSummary() {
        synchronized (lock) {
            return matches.values().stream()
                    .filter(match -> match.status == MatchStatus.IN_PROGRESS)
                    .sorted(MatchStateSummaryComparator.INSTANCE)
                    .map(MatchState::summary)
                    .toList();
        }
    }

    private void ensureTeamAvailable(String displayName, String normalizedName) {
        if (activeTeams.containsKey(normalizedName)) {
            throw new TeamAlreadyPlayingException(displayName);
        }
    }

    private MatchState requireMatch(MatchId matchId) {
        if (matchId == null || !matches.containsKey(matchId)) {
            throw new MatchNotFoundException(matchId);
        }
        return matches.get(matchId);
    }

    private static String validateTeam(String team) {
        if (team == null || team.isBlank()) {
            throw new InvalidMatchException("Team name must not be blank");
        }
        return team.strip();
    }

    private static String normalize(String team) {
        return team.toLowerCase(Locale.ROOT);
    }
}
