package org.korpora.useful;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class LangUtilitiesTest {

    @Test
    void testGetLanguage() {
        assertEquals("de", LangUtilities.getLanguage("deu").get());
        assertFalse(LangUtilities.getLanguage("xxx").isPresent());
    }

    @Test
    void testGetLanguageLocale() {
        assertEquals("deu-DE",
                LangUtilities.getLanguageLocale("de-DE", true).get());
        assertEquals("de-DE",
                LangUtilities.getLanguageLocale("de_DE", false).get());
        assertEquals("deu-DE-1",
                LangUtilities.getLanguageLocale("de_DE-1", true).get());
        assertFalse(LangUtilities.getLanguageLocale("xxx", true).isPresent());
        assertFalse(LangUtilities.getLanguageLocale("xxx", false).isPresent());
    }

    @Test
    void testToTupleTriple() {
        assertEquals("deu", LangUtilities.toThree("de"));
        assertFalse(LangUtilities.isLanguageTuple("deu"));
        assertTrue(LangUtilities.isLanguageTuple("de"));
        assertTrue(LangUtilities.isLanguageTriple("deu"));
        assertFalse(LangUtilities.isLanguageTriple("de"));
        assertEquals("nld", LangUtilities.toThree("nl"));
        assertTrue(LangUtilities.isLanguageTuple("nl"));
        assertTrue(LangUtilities.isLanguageTriple("nld"));
        assertFalse(LangUtilities.isLanguageTriple("ndl"));
        assertFalse(LangUtilities.isLanguageTriple("nl"));
    }

}