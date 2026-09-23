package com.worldcupscoreboard.implementation;

import com.worldcupscoreboard.exception.InvalidMatchException;
import com.worldcupscoreboard.model.Score;

final class ScoreUpdateValidator {
    private ScoreUpdateValidator() {
    }

    static void validate(Score current, Score updated) {
        int homeDelta = updated.home() - current.home();
        int awayDelta = updated.away() - current.away();
        boolean oneHomeGoal = Math.abs(homeDelta) == 1 && awayDelta == 0;
        boolean oneAwayGoal = homeDelta == 0 && Math.abs(awayDelta) == 1;

        if (!oneHomeGoal && !oneAwayGoal) {
            throw new InvalidMatchException(
                    "A score update must change exactly one team's score by one goal");
        }
    }
}
