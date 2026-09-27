package com.worldcupscoreboard.model;

/** Identifies the direction of a one-goal score event. */
public enum ScoreChange {
    INCREASE(1),
    DECREASE(-1);

    private final int delta;

    ScoreChange(int delta) {
        this.delta = delta;
    }

    public int delta() {
        return delta;
    }
}
