package com.smarthospital.m2_suffix;

/**
 * Module 2 (CO2): Kasai's Algorithm for Longest Common Prefix (LCP) Array
 * Used for identifying repeated medical phrases in clinical documentation.
 */
public class KasaiLCP {

    public static int[] buildLCP(String text, Integer[] sa) {
        int n = text.length();
        int[] lcp = new int[n];
        int[] invSa = new int[n];

        for (int i = 0; i < n; i++) {
            invSa[sa[i]] = i;
        }

        int k = 0;
        for (int i = 0; i < n; i++) {
            if (invSa[i] == n - 1) {
                k = 0;
                continue;
            }
            int j = sa[invSa[i] + 1];
            while (i + k < n && j + k < n && text.charAt(i + k) == text.charAt(j + k)) {
                k++;
            }
            lcp[invSa[i]] = k;
            if (k > 0) {
                k--;
            }
        }
        return lcp;
    }

    public static String findLongestRepeatedMedicalPhrase(String text, Integer[] sa, int[] lcp) {
        int maxLen = 0;
        int maxIdx = 0;
        for (int i = 0; i < lcp.length; i++) {
            if (lcp[i] > maxLen) {
                maxLen = lcp[i];
                maxIdx = sa[i];
            }
        }
        return maxLen > 0 ? text.substring(maxIdx, maxIdx + maxLen) : "";
    }
}
