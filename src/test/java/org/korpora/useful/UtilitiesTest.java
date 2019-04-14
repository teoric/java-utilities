package org.korpora.useful;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilitiesTest {

    @SuppressWarnings("deprecation")
    @Test
    void testStripSpace() {
        assertEquals("kaffee", Utilities.stripSpace("  kaffee  "));
        assertEquals("kaffee", Utilities.stripSpace(" \n kaffee \u00a0 "));
        assertThrows(IllegalArgumentException.class,
                () -> Utilities.stripSpace(null));
    }

    @Test
    void testIsEmpty() {
        assertTrue(Utilities.isEmpty(""));
        assertThrows(IllegalArgumentException.class,
                () -> Utilities.isEmpty(null));
    }

//    @Test
//    void testToArray() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    void testToList() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    void testToStream() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    void testToIterator() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    void testAttributeMap() {
//        fail("Not yet implemented");
//    }
//
//    @Test
//    void testAttributeList() {
//        fail("Not yet implemented");
//    }

}
