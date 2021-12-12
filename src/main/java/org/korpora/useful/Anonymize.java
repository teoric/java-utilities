package org.korpora.useful;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Anonymize IP v4 and v6 addresses
 *
 * @author bfi
 *
 */
@SuppressWarnings("WeakerAccess")
/**
 * utility functions for anonymizing IP addresses, v4 and v6
 *
 * @author bfi
 *
 */
public class Anonymize {
    private Anonymize() {
    }

    private static final Pattern IP4_PATTERN = Pattern.compile("^(\\d+\\.){2}",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern IP6_PATTERN = Pattern
            .compile("^(?:[0-9a-f]*:){4}", Pattern.CASE_INSENSITIVE);

    /**
     * anonymize IPv4 and IPv6 addresses
     *
     * <p>
     * Following, "Orientierungshilfe Datenschutz bei IPv6 - Hinweise für
     * Hersteller und Provider im Privatkundengeschäft", keeping first two
     * octets of IPv4 and first four hextets for IPv6.
     * </p>
     *
     * @param ip
     *     an IP address
     * @return the anonymized IP address
     */
    public static String anonymizeAddress(String ip) {
        if (ip == null) {
            return null;
        }

        Matcher ipMatcher = IP4_PATTERN.matcher(ip);
        if (ipMatcher.find()) {
            return ipMatcher.group(0) + "xxx.xxx";
        } else {
            ipMatcher = IP6_PATTERN.matcher(ip);
            if (ipMatcher.find()) {
                return ipMatcher.group(0) + "::";
            } else {
                return null;
            }
        }
    }

    /**
     * anonymize IPv4 and IPv6 addresses, delegate to
     * {@link #anonymizeAddress(String)}
     *
     * @param request
     *     a {@link Servlet} Request
     * @return the anonymized IP address
     */
    public static String anonymizeAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return anonymizeAddress(ip);
    }

}
