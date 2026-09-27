package com.worldcupscoreboard.model;

import com.worldcupscoreboard.exception.InvalidMatchException;

import java.util.Locale;

/** A validated, canonical uppercase country name. */
public record Team(String name) {
    public Team {
        if (name == null) {
            throw new InvalidMatchException("Team name must not be blank");
        }
        name = name.replaceAll("(?U)\\s+", " ").strip();

        if (!name.matches("[A-Za-z ]+")) {
            throw new InvalidMatchException(
                    "Team name may contain only English letters and spaces");
        }
        name = name.toUpperCase(Locale.ROOT);
    }
}
