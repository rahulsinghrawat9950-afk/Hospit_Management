package com.smarthospital.m2_suffix;

import java.util.Arrays;

/**
 * Module 2 (CO2): Suffix Array Construction (Prefix doubling & SA-IS)
 * Used for medical-record indexing and large-scale report searching.
 */
public class SuffixArray {
    private final String text;
    private final Integer[] suffixArray;

    public SuffixArray(String text) {
        this.text = text;
        int n = text.length();
        this.suffixArray = new Integer[n];
        for (int i = 0; i < n; i++) {
            suffixArray[i] = i;
        }

        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[i] = text.charAt(i);
        }

        int[] tempRank = new int[n];
        for (int k = 1; k < n; k *= 2) {
            final int len = k;
            Arrays.sort(suffixArray, (i1, i2) -> {
                if (rank[i1] != rank[i2]) {
                    return Integer.compare(rank[i1], rank[i2]);
                }
                int r1 = (i1 + len < n) ? rank[i1 + len] : -1;
                int r2 = (i2 + len < n) ? rank[i2 + len] : -1;
                return Integer.compare(r1, r2);
            });

            tempRank[suffixArray[0]] = 0;
            for (int i = 1; i < n; i++) {
                int prev = suffixArray[i - 1];
                int curr = suffixArray[i];
                boolean same = rank[prev] == rank[curr] &&
                        ((prev + len < n ? rank[prev + len] : -1) == (curr + len < n ? rank[curr + len] : -1));
                tempRank[curr] = tempRank[prev] + (same ? 0 : 1);
            }
            System.arraycopy(tempRank, 0, rank, 0, n);
        }
    }

    public Integer[] getSuffixArray() {
        return suffixArray;
    }

    public String getText() {
        return text;
    }

    public boolean contains(String pattern) {
        int n = text.length();
        int low = 0, high = n - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int suffixStart = suffixArray[mid];
            String sub = text.substring(suffixStart, Math.min(n, suffixStart + pattern.length()));
            int cmp = sub.compareTo(pattern);
            if (cmp == 0) return true;
            if (cmp < 0) low = mid + 1;
            else high = mid - 1;
        }
        return false;
    }
}
