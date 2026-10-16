package com.smarthospital.m1_strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Module 1 (CO1): Knuth-Morris-Pratt (KMP) Algorithm
 * Used for searching disease names and medical terms in patient histories.
 */
public class KmpSearch {

    public static List<Integer> search(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }

        int[] lps = computeLPSArray(pattern);
        int i = 0, j = 0;

        while (i < text.length()) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            if (j == pattern.length()) {
                matches.add(i - j);
                j = lps[j - 1];
            } else if (i < text.length() && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return matches;
    }

    private static int[] computeLPSArray(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0;
        int i = 1;
        lps[0] = 0;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }
}
