package org.korpora.useful;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class Anonymize {

    private static final Pattern IP4_PAT = Pattern.compile(
            "^(\\d+\\.){3}",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern IP6_PAT = Pattern.compile(
            "^(?:[0-9a-f]*:){4}",
            Pattern.CASE_INSENSITIVE);

    
    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        
        /* 
         * Following, "Orientierungshilfe Datenschutz bei IPv6 -
         * Hinweise für Hersteller und Provider im Privatkundengeschäft",
         * keeping first two octets of IPv4 and first four hextets for IPv6.
         */
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

}
