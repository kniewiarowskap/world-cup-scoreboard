package com.worldcupscoreboard.implementation;

import com.worldcupscoreboard.model.MatchSummary;

import java.util.Comparator;

final class MatchStateSummaryComparator
        implements Comparator<MatchStateSummaryComparator.SummaryEntry> {
    static final MatchStateSummaryComparator INSTANCE = new MatchStateSummaryComparator();

    private MatchStateSummaryComparator() {
    }

    @Override
    public int compare(SummaryEntry first, SummaryEntry second) {
        int totalScoreComparison = Integer.compare(
                second.summary().totalScore(),
                first.summary().totalScore());
        if (totalScoreComparison != 0) {
            return totalScoreComparison;
        }
        return Long.compare(second.startSequence(), first.startSequence());
    }

    record SummaryEntry(MatchSummary summary, long startSequence) {
    }
}
