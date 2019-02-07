package org.korpora.useful;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * utility functions related to ISO-639-related language handling
 *
 * @author bfi
 *
 */

public class LangUtilities {
    private static final String LANGNAMES_PATH = "languages-639-most-tolerant.json";
    private static final String LANGCODES_3_PATH = "language-codes-three-letters.txt";
    private static final String LANGCODES_2_PATH = "language-codes-two-letters.txt";

    /**
     * what separates language and country codes etc., "de-DE" (German in
     * Germany), "nld_BE" (Dutch in Belgium)
     */
    private static final Pattern LOCALE_SEPARATOR = Pattern.compile("[_-]+");

    /**
     * map from language names / letter triples/tuples to terminological
     * ISO-639-2 code.
     */
    private static Map<String, String> languageMap;

    /**
     * valid terminological ISO-639-2 three letter codes
     *
     * see {@link #languageCodesThree} for a list including bibliographic
     * variants
     */
    private static Set<String> languageTriples;

    /*
     * prepare variables
     */
    static {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream str = LangUtilities.class.getClassLoader()
                .getResourceAsStream(LANGNAMES_PATH)) {
            languageMap = mapper.readValue(str,
                    new TypeReference<Map<String, String>>() {
                    });
            languageTriples = languageMap.keySet().stream().distinct()
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    /**
     * valid terminological ISO-639-2 three letter codes, including
     * bibliographic variants
     */
    private static Set<String> languageCodesThree = new HashSet<>();
    static {
        try (InputStream str = LangUtilities.class.getClassLoader()
                .getResourceAsStream(LANGCODES_3_PATH)) {
            InputStreamReader strR = new InputStreamReader(str);
            BufferedReader strRR = new BufferedReader(strR);
            strRR.lines().forEach(l -> languageCodesThree.add(l));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    /**
     * valid terminological ISO-639-1 two letter codes
     */
    private static Set<String> languageCodesTwo = new HashSet<>();
    static {
        try (InputStream str = LangUtilities.class.getClassLoader()
                .getResourceAsStream(LANGCODES_2_PATH)) {
            InputStreamReader strR = new InputStreamReader(str);
            BufferedReader strRR = new BufferedReader(strR);
            strRR.lines().forEach(l -> languageCodesTwo.add(l));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * split a potential language locale, keeping only the first part
     *
     * @param lang
     *            the language string, e.g. "de-DE"
     * @return the normalized language, e.g. "deu"
     */
    private static String splitLang(String lang) {
        // allow for "locale" like deu-DE
        return LOCALE_SEPARATOR.split(lang)[0];
    }

    /**
     * Can we map {@code lang} to a standardised ISO 639-1 triple?
     *
     * @param langu
     *            the language name / two or three letter code
     * @return whether
     */
    public static boolean isLanguage(String langu) {
        String lang = splitLang(langu);
        return languageMap.containsKey(lang.toLowerCase());
    }

    /**
     * Get the (terminological) three letter ISO-639-1 code for language
     *
     * @param langu
     *            the language name / two or three letter code
     * @return the three letter code as an Optional
     */
    public static Optional<String> getLanguage(String langu) {
        String lang = splitLang(langu);
        return Optional.ofNullable(languageMap.get(lang.toLowerCase()));
    }

    /**
     * Get the (terminological) three letter ISO-639-1 code for language
     *
     * @param lang
     *            the language name / two or three letter code
     * @param defaultL
     *            the default code to return if {@code lang} is no discernible
     *            language
     * @return the three letter code, or the default
     */
    public static String getLanguage(String lang, String defaultL) {
        return languageMap.getOrDefault(lang.toLowerCase(), defaultL);
    }

    /**
     * Get the (terminological) three letter ISO-639-1 code for language,
     * potentially with locale rest
     *
     * @param langu
     *            the language name / two or three letter code
     * @return the three letter code as an Optional
     */
    public static Optional<String> getLanguageLocale(String langu) {
        String[] lang = LOCALE_SEPARATOR.split(langu);
        Optional<String> language = Optional
                .ofNullable(languageMap.get(lang[0].toLowerCase()));
        if (lang.length > 1) {
            language = language.map(s -> s + "-" + String.join("-",
                    Arrays.copyOfRange(lang, 1, lang.length)));
        }
        return language;
    }

    /**
     * Get the (terminological) three letter ISO-639-1 code for language,
     * potentially with locale
     *
     * @param lang
     *            the language name / two or three letter code
     * @param defaultL
     *            the default code to return if {@code lang} is no discernible
     *            language
     * @return the three letter code, or the default
     */
    public static String getLanguageLocale(String lang, String defaultL) {
        Optional<String> optLang = getLanguageLocale(lang);
        if (optLang.isPresent()) {
            return optLang.get();
        } else {
            return defaultL;
        }
    }

    /**
     * Is this an ISO 639-2 three letter code?
     *
     * @param lang
     *            the language code
     * @return whether
     *
     */
    public static boolean isLanguageTriple(String lang) {
        return languageCodesThree.contains(lang.toLowerCase());
    }

    /**
     * Is this a terminological ISO 639-2 three letter code (i.e. a key in
     * languageMap)
     *
     * @param lang
     *            the language code
     * @return whether
     */
    public static boolean isTerminologicalLanguageTriple(String lang) {
        return languageTriples.contains(lang.toLowerCase());
    }

    /**
     * Is this an ISO 639-1 two letter code
     *
     * @param lang
     *            the language code
     * @return whether
     */
    public static boolean isLanguageTuple(String lang) {
        return languageCodesTwo.contains(lang.toLowerCase());
    }

}
