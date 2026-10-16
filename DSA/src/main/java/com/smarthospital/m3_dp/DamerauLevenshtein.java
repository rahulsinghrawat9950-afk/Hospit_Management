package com.smarthospital.m3_dp;

import java.util.HashMap;
import java.util.Map;

/**
 * Module 3 (CO3): Damerau-Levenshtein Distance
 * Handles insertions, deletions, substitutions, and adjacent character transpositions.
 */
public class DamerauLevenshtein {

    public static int compute(String s1, String s2) {
        if (s1 == null || s2 == null) return -1;
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 2][n + 2];
        int maxDist = m + n;

        dp[0][0] = maxDist;
        for (int i = 0; i <= m; i++) {
            dp[i + 1][0] = maxDist;
            dp[i + 1][1] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j + 1] = maxDist;
            dp[1][j + 1] = j;
        }

        Map<Character, Integer> da = new HashMap<>();

        for (int i = 1; i <= m; i++) {
            int db = 0;
            for (int j = 1; j <= n; j++) {
                int k = da.getOrDefault(s2.charAt(j - 1), 0);
                int l = db;
                int cost = 0;
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    db = j;
                } else {
                    cost = 1;
                }
                dp[i + 1][j + 1] = Math.min(
                        dp[i][j] + cost,
                        Math.min(dp[i + 1][j] + 1,
                                Math.min(dp[i][j + 1] + 1,
                                        dp[k][l] + (i - k - 1) + 1 + (j - l - 1)))
                );
            }
            da.put(s1.charAt(i - 1), i);
        }
        return dp[m + 1][n + 1];
    }
}
