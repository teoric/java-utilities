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
 *
 */

@SuppressWarnings("WeakerAccess")
public class LangUtilities {
    private static final String LANGNAMES_PATH = "languages-639-most-tolerant.json";
    private static final String LANGCODES_3_PATH = "language-codes-three-letters.txt";
    private static final String LANGCODES_2_PATH = "language-codes-two-letters.txt";
    private static final String LANGCODES_2_3_PATH = "language-list-639-1-to-639-2.json";

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

    private static final Map<String, String> threeToTwo;

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
            threeToTwo = mapper.readValue(str,
                    new TypeReference<Map<String, String>>() {
                    });
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    /**
     * valid terminological ISO-639-2 three letter codes, including
     * bibliographic variants
     */
    private static final Set<String> languageCodesThree = new HashSet<>();
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
    /**
     * valid terminological ISO-639-1 two letter codes
     */
    private static final Set<String> languageCodesTwo = new HashSet<>();
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
     * return three letter code if two letter code, else keep as is
     * @param lang
     *           a potential letter code
     * @return three letter code, or original string
     */
    public static String toThree(String lang) {
        return threeToTwo.getOrDefault(lang, lang);
    }

    /**
     * Get the (terminological) three letter ISO-639-1 code for language,
     * potentially with locale rest
     *
     * @param langu
     *            the language name / two or three letter code
     * @param maxComponents
     *            the maximal number of language + locale components
     * @return the three letter code as an Optional
     */
    public static Optional<String> getLanguageLocale(String langu, int maxComponents, boolean forceThree) {
        String[] lang = LOCALE_SEPARATOR.split(langu);
        int components = Math.min(maxComponents, lang.length);
        Optional<String> language = Optional
                .ofNullable(languageMap.get(lang[0].toLowerCase()));
        if (forceThree && language.isPresent() && language.get().length() == 2){
               language.map(threeToTwo::get);
        }
        if (lang.length > 1) {
            language = language.map(s -> s + "-" + String.join("-",
                    Arrays.copyOfRange(lang, 1, components)));
        }
        return language;
    }

    /**
     * Get the (terminological) three letter ISO-639-1 code for language,
     * potentially with locale rest
     *
     * @param lang
     *            the language name / two or three letter code
     * @return the three letter code as an Optional
     */
    public static Optional<String> getLanguageLocale(String lang,
                                                     boolean forceThree) {
        return getLanguageLocale(lang, Integer.MAX_VALUE, forceThree);
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
     * @param maxComponents
     *            the maximal number of language + locale components
     * @return the three letter code, or the default
     */
    public static String getLanguageLocale(
            String lang, String defaultL, int maxComponents, boolean forceThree) {
        Optional<String> optLang = getLanguageLocale(lang, maxComponents, forceThree);
        return optLang.orElse(defaultL);
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
    public static String getLanguageLocale(String lang, String defaultL,
                                           boolean forceThree) {
        return getLanguageLocale(lang, defaultL, Integer.MAX_VALUE, forceThree);
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
        return languageCodesThree.contains(lang.toLowerCase());
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
