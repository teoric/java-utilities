package org.korpora.useful;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnonymizeTest {

        @Test
        void testAnonymization() {
            assertNull(Anonymize.getRemoteAddress("x"));
            assertNull(Anonymize.getRemoteAddress("blah.blah.blah.blah"));
            assertEquals("127.0.xxx.xxx",
                    Anonymize.getRemoteAddress("127.0.0.1"));
            assertEquals("2001:0db8:85a3:08d3:::",
                    Anonymize.getRemoteAddress("2001:0db8:85a3:08d3::0370:7344"));
        }
}
