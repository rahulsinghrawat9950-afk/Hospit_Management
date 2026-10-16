package com.smarthospital.m3_dp;

import java.util.Arrays;

/**
 * Module 3 (CO3): Bitmask Dynamic Programming
 * Optimal selection of subset of diagnostic tests under budget and resource constraints.
 */
public class BitmaskDP {

    public static int optimizeDiagnosticTests(int[] testCosts, int[] diagnosticValue, int budget) {
        int n = testCosts.length;
        int maxMask = 1 << n;
        int[] dp = new int[maxMask];
        Arrays.fill(dp, -1);
        dp[0] = 0;

        int maxValue = 0;
        for (int mask = 0; mask < maxMask; mask++) {
            if (dp[mask] == -1) continue;

            int currentCost = 0;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    currentCost += testCosts[i];
                }
            }

            if (currentCost <= budget) {
                maxValue = Math.max(maxValue, dp[mask]);
            }

            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) == 0) {
                    int nextMask = mask | (1 << i);
                    dp[nextMask] = Math.max(dp[nextMask], dp[mask] + diagnosticValue[i]);
                }
            }
        }
        return maxValue;
    }
}
