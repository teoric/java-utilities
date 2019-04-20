package org.korpora.useful;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

/**
 * utility functions related to ISO-639-related language handling
 *
 * @author bfi
 */

@SuppressWarnings("WeakerAccess")
public class LangUtilities {
    private static final String LANGNAMES_PATH =
            "languages-639-most-tolerant.json";
    private static final String LANGCODES_3_PATH =
            "language-codes-three-letters.txt";
    private static final String LANGCODES_2_PATH =
            "language-codes-two-letters.txt";
    private static final String LANGCODES_2_3_PATH =
            "language-list-639-1-to-639-2.json";

    /**
     * what separates language and country codes etc., "de-DE" (German in
     * Germany), "nld_BE" (Dutch in Belgium)
     */
    private static final Pattern LOCALE_SEPARATOR = Pattern.compile("[_-]+");

    /**
     * map from language names / letter triples/tuples to
     * ISO-639-1 code terminological
     * ISO-639-2 code.
     */
    private static final Map<String, String> languageMap;

    private static final Map<String, String> twoToThree;
    /**
     * valid terminological ISO-639-2 three letter codes, including
     * bibliographic variants
     */
    private static final Set<String> languageCodesThree = new HashSet<>();
    /**
     * valid terminological ISO-639-1 two letter codes
     */
    private static final Set<String> languageCodesTwo = new HashSet<>();

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
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        try (InputStream str = LangUtilities.class.getClassLoader()
                .getResourceAsStream(LANGCODES_2_3_PATH)) {
            twoToThree = mapper.readValue(str,
                    new TypeReference<Map<String, String>>() {
                    });
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    static {
        try (InputStream str = LangUtilities.class.getClassLoader()
                .getResourceAsStream(LANGCODES_3_PATH)) {
            assert str != null;
            InputStreamReader strR = new InputStreamReader(str);
            BufferedReader strRR = new BufferedReader(strR);
            strRR.lines().forEach(languageCodesThree::add);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    static {
        try (InputStream str = LangUtilities.class.getClassLoader()
                .getResourceAsStream(LANGCODES_2_PATH)) {
            assert str != null;
            InputStreamReader strR = new InputStreamReader(str);
            BufferedReader strRR = new BufferedReader(strR);
            strRR.lines().forEach(languageCodesTwo::add);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * split a potential language locale, keeping only the first part
     *
     * @param lang
     *         the language string, e.g. "de-DE"
     * @return the normalized language, e.g. "deu"
     */
    private static String splitLang(String lang) {
        // allow for "locale" like deu-DE
        return LOCALE_SEPARATOR.split(lang)[0];
    }

    /**
     * Can we map {@code lang} to a standardised ISO 639-1 triple?
     *
     * @param language
     *         the language name / two or three letter code
     * @return whether
     */
    public static boolean isLanguage(String language) {
        String lang = splitLang(language);
        return languageMap.containsKey(lang.toLowerCase());
    }

    /**
     * Get the ISO-639 two- or three-letter code for a language
     *
     * @param language
     *         the language name / two or three letter code
     * @return the shortest letter code as an Optional
     */
    public static Optional<String> getLanguage(String language) {
        String lang = splitLang(language);
        return Optional.ofNullable(languageMap.get(lang.toLowerCase()));
    }


    /**
     * Get the ISO-639 two- or three-letter code for a language
     *
     * @param language
     *         the language name / two or three letter code
     * @return the shortest letter code as string Optional
     */
    public static String getLanguageString(String language) {
        String lang = splitLang(language);
        return languageMap.get(lang.toLowerCase());
    }

    /**
     * Get the ISO-639 two- or three-letter code for a language
     *
     * @param lang
     *         the language name / two or three letter code
     * @param defaultL
     *         the default code to return if {@code lang} is no discernible
     *         language
     * @return the letter code, or the default
     */
    public static String getLanguage(String lang, String defaultL) {
        return languageMap.getOrDefault(lang.toLowerCase(), defaultL);
    }

    /**
     * return three letter code if two letter code, else keep as is
     *
     * @param language
     *         a potential letter code
     * @return three letter code, or original string
     */
    public static String toThree(String language) {
        return twoToThree.getOrDefault(language, language);
    }

    /**
     * Get the (terminological) three letter ISO-639-1 code for language,
     * potentially with locale rest
     *
     * @param language
     *         the language name / two or three letter code
     * @param maxComponents
     *         the maximal number of language + locale components
     * @return the three letter code as an Optional
     */
    public static Optional<String> getLanguageLocale(String language,
                                                     int maxComponents,
                                                     boolean forceThree) {
        String[] lang = LOCALE_SEPARATOR.split(language);
        int components = Math.min(maxComponents, lang.length);
        Optional<String> languageO = Optional
                .ofNullable(languageMap.get(lang[0].toLowerCase()));
        if (forceThree && languageO.isPresent() && languageO.get().length() == 2) {
            languageO = Optional.of(twoToThree.get(languageO.get()));
        }
        if (lang.length > 1) {
            languageO = languageO.map(s -> s + "-" + String.join("-",
                    Arrays.copyOfRange(lang, 1, components)));
        }
        return languageO;
    }

    /**
     * Get the (terminological) three letter ISO-639-1 code for language,
     * potentially with locale rest
     *
     * @param language
     *         the language name / two or three letter code
     * @return the three letter code as an Optional
     */
    public static Optional<String> getLanguageLocale(String language,
                                                     boolean forceThree) {
        return getLanguageLocale(language, Integer.MAX_VALUE, forceThree);
    }

    /**
     * Get the (terminological) three letter ISO-639-1 code for language,
     * potentially with locale
     *
     * @param language
     *         the language name / two or three letter code
     * @param defaultL
     *         the default code to return if {@code language} is no discernible
     *         language
     * @param maxComponents
     *         the maximal number of language + locale components
     * @return the three letter code, or the default
     */
    public static String getLanguageLocale(
            String language, String defaultL, int maxComponents,
            boolean forceThree) {
        Optional<String> optLang = getLanguageLocale(language, maxComponents,
                forceThree);
        return optLang.orElse(defaultL);
    }

    /**
     * Get the (terminological) three letter ISO-639-1 code for language,
     * potentially with locale
     *
     * @param language
     *         the language name / two or three letter code
     * @param defaultL
     *         the default code to return if {@code language} is no discernible
     *         language
     * @return the three letter code, or the default
     */
    public static String getLanguageLocale(String language, String defaultL,
                                           boolean forceThree) {
        return getLanguageLocale(language, defaultL, Integer.MAX_VALUE, forceThree);
    }

    /**
     * Is this an ISO 639-2 three letter code?
     *
     * @param language
     *         the language code
     * @return whether
     */
    public static boolean isLanguageTriple(String language) {
        return languageCodesThree.contains(language.toLowerCase());
    }

    /**
     * Is this a terminological ISO 639-2 three letter code (i.e. a key in
     * languageMap)
     *
     * @param language
     *         the language code
     * @return whether
     */
    public static boolean isTerminologicalLanguageTriple(String language) {
        return languageCodesThree.contains(language.toLowerCase());
    }

    /**
     * Is this an ISO 639-1 two letter code
     *
     * @param language
     *         the language code
     * @return whether
     */
    public static boolean isLanguageTuple(String language) {
        return languageCodesTwo.contains(language.toLowerCase());
    }

}
