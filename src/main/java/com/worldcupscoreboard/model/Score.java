package com.worldcupscoreboard.model;

/**
 * Immutable non-negative score for the home and away teams.
 *
 * @param home the home-team score
 * @param away the away-team score
 */
public record Score(int home, int away) {
    public Score {
        if (home < 0 || away < 0) {
            throw new IllegalArgumentException("Scores cannot be negative");
        }
    }

    /** @return the combined home and away score */
    public int total() {
        return home + away;
    }
}
