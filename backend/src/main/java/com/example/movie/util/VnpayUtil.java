package com.example.movie.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VnpayUtil {
    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKey);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("HmacSHA512 error", e);
        }
    }

    public static String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    /** build query string & hash data with sorted keys */
    public static String buildQueryAndHash(Map<String, String> params, String secret, StringBuilder outQuery) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder hashData = new StringBuilder();
        for (int i=0; i<keys.size(); i++) {
            String k = keys.get(i);
            String v = params.get(k);
            if (v == null || v.isEmpty()) continue;
            hashData.append(k).append('=').append(urlEncode(v));
            outQuery.append(k).append('=').append(urlEncode(v));
            if (i < keys.size()-1) { hashData.append('&'); outQuery.append('&'); }
        }
        return hmacSHA512(secret, hashData.toString());
    }
}
