package com.worldcupscoreboard.implementation;

import java.util.Comparator;

final class MatchStateSummaryComparator implements Comparator<MatchState> {
    static final MatchStateSummaryComparator INSTANCE =
            new MatchStateSummaryComparator();

    private MatchStateSummaryComparator() {
    }

    @Override
    public int compare(MatchState first, MatchState second) {
        int totalScoreComparison = Integer.compare(
                second.score.total(),
                first.score.total());
        if (totalScoreComparison != 0) {
            return totalScoreComparison;
        }
        return Long.compare(second.startSequence, first.startSequence);
    }
}
