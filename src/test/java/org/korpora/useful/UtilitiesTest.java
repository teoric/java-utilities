package org.korpora.useful;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UtilitiesTest {

    @SuppressWarnings("deprecation")
    @Test
    void testStripSpace() {
        assertEquals("kaffee", Utilities.stripSpace("  kaffee  "));
        assertEquals("kaffee", Utilities.stripSpace(" \n kaffee \u00a0 "));
        assertThrows(IllegalArgumentException.class,
                () -> Utilities.stripSpace(null));
    }

    @SuppressWarnings("deprecation")
    @Test
    void testSpace() {
        assertEquals("_kaffee_", Utilities.spaceScore("  kaffee  "));
        assertEquals("-kaffee-", Utilities.spaceBar("  kaffee  "));
        assertEquals("-kaffee-", Utilities.spaceBar("  kaffee   "));
        assertEquals("-kaffee-", Utilities.spaceBar(" \n kaffee \u00a0 "));
        assertEquals("*kaffee*", Utilities.spaceBar(" \n kaffee \u00a0 ", "*"));
        assertThrows(IllegalArgumentException.class,
                () -> Utilities.stripSpace(null));
    }


    @Test
    void testRemoveSpace() {
        assertEquals("kaffee", Utilities.removeSpace("  ka ff  ee  "));
        assertEquals("kaffee", Utilities.removeSpace(" \n kaf\nfee \u00a0 "));
        assertThrows(IllegalArgumentException.class,
                () -> Utilities.removeSpace(null));
    }

    @Test
    void testIsEmpty() {
        assertTrue(Utilities.isEmpty(""));
        assertThrows(IllegalArgumentException.class,
                () -> Utilities.isEmpty(null));
    }
}
