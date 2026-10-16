package com.smarthospital.m1_strings;

import java.util.ArrayList;
import java.util.List;

/**
 * Module 1 (CO1): Z-Function Algorithm
 * Used for identifying repeated medical phrases and prefix matches in clinical notes.
 */
public class ZFunction {

    public static int[] calculateZ(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;
        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }
        return z;
    }

    public static List<Integer> searchPattern(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty()) return matches;

        String concat = pattern + "$" + text;
        int[] z = calculateZ(concat);
        int patLen = pattern.length();

        for (int i = 0; i < z.length; i++) {
            if (z[i] == patLen) {
                matches.add(i - patLen - 1);
            }
        }
        return matches;
    }
}
