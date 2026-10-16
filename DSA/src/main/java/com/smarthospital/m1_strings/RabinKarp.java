package com.smarthospital.m1_strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Module 1 (CO1): Rabin-Karp Algorithm
 * Used for diagnostic code searching (e.g. ICD-10/ICD-11 codes) in medical documents.
 */
public class RabinKarp {
    private static final int PRIME = 101;
    private static final int RADIX = 256;

    public static List<Integer> search(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        int m = pattern.length();
        int n = text.length();
        long patHash = 0;
        long txtHash = 0;
        long h = 1;

        for (int i = 0; i < m - 1; i++) {
            h = (h * RADIX) % PRIME;
        }

        for (int i = 0; i < m; i++) {
            patHash = (RADIX * patHash + pattern.charAt(i)) % PRIME;
            txtHash = (RADIX * txtHash + text.charAt(i)) % PRIME;
        }

        for (int i = 0; i <= n - m; i++) {
            if (patHash == txtHash) {
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) matches.add(i);
            }

            if (i < n - m) {
                txtHash = (RADIX * (txtHash - text.charAt(i) * h) + text.charAt(i + m)) % PRIME;
                if (txtHash < 0) {
                    txtHash = (txtHash + PRIME);
                }
            }
        }
        return matches;
    }
}
