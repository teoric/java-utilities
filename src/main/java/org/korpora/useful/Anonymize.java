package org.korpora.useful;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("WeakerAccess")
public class Anonymize {

    private static final Pattern IP4_PAT = Pattern.compile(
            "^(\\d+\\.){3}",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern IP6_PAT = Pattern.compile(
            "^(?:[0-9a-f]*:){4}",
            Pattern.CASE_INSENSITIVE);

    /**
     * anonymize IPv4 and IPv6 addresses
     *
     * <p>Following, "Orientierungshilfe Datenschutz bei IPv6 -
     * Hinweise für Hersteller und Provider im Privatkundengeschäft",
     * keeping first two octets of IPv4 and first four hextets for IPv6.</p>
     *
     * @param ip – an IP address
     * @return the anonymized IP address
     */
    public static String getRemoteAddress(String ip) {
        if (ip == null) {
            return null;
        }

        Matcher ip_mat;
        ip_mat = IP4_PAT.matcher(ip);
        if (ip_mat.find()) {
            return ip_mat.group(0) + "xxx.xxx";
        } else {
            ip_mat = IP6_PAT.matcher(ip);
            if (ip_mat.find()) {
                return ip_mat.group(0) + ":::";
            } else {
                return null;
            }
        }
    }

    /**
     * anonymize IPv4 and IPv6 addresses, delegate to {@link #getRemoteAddress(String)}
     * @param request – a Servlet Request
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
        return getRemoteAddress(ip);
    }

}
