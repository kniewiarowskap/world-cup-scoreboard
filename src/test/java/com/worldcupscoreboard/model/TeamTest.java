package com.worldcupscoreboard.model;

import com.worldcupscoreboard.exception.InvalidMatchException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TeamTest {
    @Test
    void trimsCollapsesWhitespaceAndStoresUppercaseName() {
        Team team = new Team("  Mexico  ");

        assertEquals("MEXICO", team.name());
        assertEquals(new Team("mexico"), team);
        assertEquals("UNITED STATES", new Team("  United   States ").name());
    }

    @Test
    void acceptsEnglishLettersButRejectsBlankOrNonEnglishCharacters() {
        assertThrows(InvalidMatchException.class, () -> new Team(null));
        assertThrows(InvalidMatchException.class, () -> new Team("  "));
        assertThrows(InvalidMatchException.class, () -> new Team("Congo-DR"));
        assertThrows(InvalidMatchException.class, () -> new Team("Country 2"));
        assertThrows(InvalidMatchException.class, () -> new Team("Côte D'Ivoire"));
        assertThrows(InvalidMatchException.class, () -> new Team("España"));
        assertThrows(InvalidMatchException.class, () -> new Team("Россия"));
        assertThrows(InvalidMatchException.class, () -> new Team("ßpain"));
        assertEquals("IVORY COAST", new Team("Ivory Coast").name());
    }
}
