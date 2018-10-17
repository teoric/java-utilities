package org.korpora.useful;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UtilitiesTest {

    @SuppressWarnings("deprecation")
    @Test
    void testStripSpace() {
        assertEquals("kaffee", Utilities.stripSpace("  kaffee  "));
        assertEquals("kaffee", Utilities.stripSpace("  kaffee "));
        assertThrows(IllegalArgumentException.class, () -> Utilities.stripSpace(null));
    }

    @SuppressWarnings("deprecation")
    @Test
    void testIsEmpty() {
        assertTrue(Utilities.isEmpty(""));
        assertThrows(IllegalArgumentException.class, () -> Utilities.isEmpty(null));
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
