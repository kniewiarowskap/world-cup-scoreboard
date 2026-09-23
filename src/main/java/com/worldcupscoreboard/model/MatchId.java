package com.worldcupscoreboard.model;

import java.util.Objects;
import java.util.UUID;

public record MatchId(UUID value) {
    public MatchId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static MatchId generate() {
        return new MatchId(UUID.randomUUID());
    }
}
