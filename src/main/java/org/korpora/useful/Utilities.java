package org.korpora.useful;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.*;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Some collected utilities
 *
 * @author Bernhard Fisseni (bernhard.fisseni@uni-due.de)
 */
@SuppressWarnings("WeakerAccess")
public class Utilities {

    /**
     * whether we are on Windows
     * @return whether
     */
    public static boolean onWindOs() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    /**
     * for use in third place of
     * {@link java.util.stream.Collectors#toMap(java.util.function.Function, java.util.function.Function, BinaryOperator, java.util.function.Supplier)}.
     * end merging if duplicate key found.
     */
    public static final BinaryOperator<String> strCollider = (u, v) -> {
        throw new IllegalStateException(String.format("Duplicate key «%s»", u));
    };
    /**
     * for use in third place of
     * {@link java.util.stream.Collectors#toMap(java.util.function.Function, java.util.function.Function, BinaryOperator, java.util.function.Supplier)}.
     * end merging if duplicate key found.
     */
    public static final BinaryOperator<Integer> intCollider = (u, v) -> {
        throw new IllegalStateException(
                String.format("Duplicate key «%s»", u.toString()));
    };
    /**
     * for use in third place of
     * {@link java.util.stream.Collectors#toMap(java.util.function.Function, java.util.function.Function, BinaryOperator, java.util.function.Supplier)}.
     * end merging if duplicate key found.
     */
    public static final BinaryOperator<?> anyCollider = (u, v) -> {
        throw new IllegalStateException(
                String.format("Duplicate key «%s»", u.toString()));
    };
    // Java is crazy: \p{Z} does not work as intended
    private static final Pattern SPACE = Pattern
            .compile("[\\p{javaWhitespace}\\p{Z}]+", Pattern.MULTILINE);
    private static final Pattern SPACE_START = Pattern
            .compile("\\A" + SPACE + "+", Pattern.MULTILINE);
    private static final Pattern SPACE_END = Pattern.compile("" + SPACE + "\\Z",
            Pattern.MULTILINE);
    // Regular expression from https://www.regular-expressions.info/unicode.html
    private static final Pattern GRAPHEME = Pattern.compile("\\P{M}\\p{M}*+");
    private static final Pattern NON_EMPTY = Pattern.compile("\\P{Space}");

    /**
     * count Unicode “graphemes” in String
     *
     * @param s
     *         the string
     * @return the count of graphemes
     */
    public static int countGraphemes(String s) {
        int i = 0;
        Matcher graphMatcher = GRAPHEME.matcher(s);
        while (graphMatcher.find()) {
            i++;
        }
        return i;
    }

    /**
     * Strip space from String – Unicode-aware.
     * <p>
     * since Java 11, use #{@link String}::strip. When sure that Unicode does
     * not matter, use #{@link String#trim()}.
     *
     * @param s
     *         an innocent String
     * @return the stripped s
     * @deprecated use #{@link StringUtils#strip(String)}
     */
    @Deprecated
    public static String stripSpace(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        String ret = SPACE_START.matcher(s).replaceAll("");
        ret = SPACE_END.matcher(ret).replaceAll("");
        return ret;
    }

    /**
     * Remove space from String – Unicode-aware.
     *
     * @param s
     *         an innocent String
     * @return the stripped s
     */
    public static String removeSpace(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        return SPACE.matcher(s).replaceAll("");
    }

    /**
     * Determine if String is non-empty, i.e., contains non-white-space content
     *
     * @param s
     *         an innocent string
     * @return whether s is empty (contains only space)
     */
    public static boolean isEmpty(String s) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        return !NON_EMPTY.matcher(s).find();
    }

    /**
     * increase a counter in a Map
     *
     * @param <T>
     *         the type of the counted thing
     * @param map
     *         the map
     * @param key
     *         the counted thing
     */
    public static <T> void incCounter(Map<? super T, Integer> map, T key) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + 1);
        } else {
            map.put(key, 1);
        }
    }

    /**
     * print lines to file
     *
     * @param lines
     *         a list of lines
     * @param fileName
     *         a file name
     * @throws IOException
     *         in case of problems
     */
    public static void linesToFile(List<String> lines, String fileName) throws IOException {
        linesToFile(lines, new File(fileName));
    }

    /**
     * print lines to file
     *
     * @param lines
     *         a list of lines
     * @param path
     *         a path
     * @throws IOException
     *         in case of problems
     */
    public static void linesToFile(List<String> lines, Path path) throws IOException {
        linesToFile(lines, path.toFile());
    }

    /**
     * print lines to file
     *
     * @param lines
     *         a list of lines
     * @param file
     *         a file
     * @throws IOException
     *         in case of problems
     */
    public static void linesToFile(List<String> lines, File file) throws IOException {
        try (FileWriter fw = new FileWriter(file)) {
            PrintWriter pw = new PrintWriter(fw);
            lines.forEach(pw::println);
        }

    }

    

    

    /*
     * two convenience methods from
     * https://gist.github.com/sachin-handiekar/1346229
     */

}
