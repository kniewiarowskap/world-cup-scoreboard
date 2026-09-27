package com.worldcupscoreboard.model;

import static java.util.Objects.requireNonNull;

import java.util.UUID;

/**
 * Immutable identifier for a scoreboard match.
 *
 * @param value the UUID backing this identifier
 */
public record MatchId(UUID value) {
    public MatchId {
        requireNonNull(value, "value must not be null");
    }

    /**
     * Creates a new randomly generated match identifier.
     *
     * @return a unique match identifier
     */
    public static MatchId generate() {
        return new MatchId(UUID.randomUUID());
    }
}
