package org.korpora.useful;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class LangUtilitiesTest {

    @Test
    void testGetLanguage() {
        assertEquals("deu", LangUtilities.getLanguage("deu").get());
        assertFalse(LangUtilities.getLanguage("xxx").isPresent());
    }

    @Test
    void testGetLanguageLocale() {
        assertEquals("deu-DE", LangUtilities.getLanguageLocale("de-DE").get());
        assertEquals("deu-DE", LangUtilities.getLanguageLocale("de_DE").get());
        assertEquals("deu-DE-1",
                LangUtilities.getLanguageLocale("de_DE-1").get());
        assertFalse(LangUtilities.getLanguageLocale("xxx").isPresent());
    }

}
