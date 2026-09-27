package com.worldcupscoreboard.model;

import com.worldcupscoreboard.exception.InvalidMatchException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TeamTest {
    @ParameterizedTest
    @MethodSource("validNames")
    void normalizesEnglishTeamNames(String input, String expectedName) {
        Team team = new Team(input);

        assertEquals(expectedName, team.name());
        assertEquals(new Team(expectedName), team);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "  ",
            "Congo-DR",
            "Country 2",
            "Côte D'Ivoire",
            "España",
            "Россия",
            "ßpain"
    })
    void rejectsBlankOrNonEnglishTeamNames(String input) {
        assertThrows(InvalidMatchException.class, () -> new Team(input));
    }

    private static Stream<Arguments> validNames() {
        return Stream.of(
                Arguments.of("  Mexico  ", "MEXICO"),
                Arguments.of("mexico", "MEXICO"),
                Arguments.of("  United   States ", "UNITED STATES"),
                Arguments.of("Ivory Coast", "IVORY COAST"));
    }
}
