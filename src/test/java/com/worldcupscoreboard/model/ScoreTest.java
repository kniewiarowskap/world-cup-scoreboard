package com.worldcupscoreboard.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScoreTest {
    @Test
    void calculatesTotalScore() {
        assertEquals(2, new Score(1, 1).total());
    }

    @Test
    void rejectsNegativeScoresAndNullSide() {
        assertThrows(IllegalArgumentException.class, () -> new Score(-1, 0));
    }
}
