package com.smarthospital.m7_parallel;

import java.util.*;

/**
 * Module 7 (CO6): Blelloch Scan for Patient-Statistics Aggregation.
 * Parallel work-efficient prefix-sum algorithm:
 * - Up-sweep (parallel reduction) phase: Work O(n), Span O(log n)
 * - Down-sweep phase: Work O(n), Span O(log n)
 * Aggregates cumulative medication dosages, cumulative patient wait times, and clinical running totals.
 */
public class BlellochScan {

    public static class ScanResult {
        public final long[] original;
        public final long[] exclusiveScan;
        public final long[] inclusiveScan;
        public final long totalSum;
        public final int work;
        public final int span;
        public final List<String> phaseLogs;

        public ScanResult(long[] original, long[] exclusiveScan, long[] inclusiveScan,
                          long totalSum, int work, int span, List<String> phaseLogs) {
            this.original = original;
            this.exclusiveScan = exclusiveScan;
            this.inclusiveScan = inclusiveScan;
            this.totalSum = totalSum;
            this.work = work;
            this.span = span;
            this.phaseLogs = phaseLogs;
        }
    }

    public static ScanResult scan(long[] input) {
        if (input == null || input.length == 0) {
            return new ScanResult(new long[0], new long[0], new long[0], 0, 0, 0, Collections.emptyList());
        }

        int origLen = input.length;
        int n = nextPowerOfTwo(origLen);
        long[] tree = new long[n];
        System.arraycopy(input, 0, tree, 0, origLen);

        List<String> logs = new ArrayList<>();
        logs.add(String.format("Input size: %d, padded to power-of-2 size: %d", origLen, n));

        int logN = (int) (Math.log(n) / Math.log(2));
        int workCount = 0;
        int spanCount = 2 * logN;

        // Phase 1: Up-Sweep (Reduce Phase)
        for (int d = 0; d < logN; d++) {
            int stride = 1 << (d + 1);
            int half = 1 << d;
            int opsInRound = 0;
            for (int k = 0; k < n; k += stride) {
                tree[k + stride - 1] += tree[k + half - 1];
                opsInRound++;
                workCount++;
            }
            logs.add(String.format("Up-Sweep step %d (stride %d): %d parallel addition(s)", d + 1, stride, opsInRound));
        }

        long totalSum = tree[n - 1];
        logs.add("Up-Sweep complete. Aggregated root sum: " + totalSum);

        // Clear root to identity (0) for exclusive scan
        tree[n - 1] = 0;

        // Phase 2: Down-Sweep Phase
        for (int d = logN - 1; d >= 0; d--) {
            int stride = 1 << (d + 1);
            int half = 1 << d;
            int opsInRound = 0;
            for (int k = 0; k < n; k += stride) {
                long t = tree[k + half - 1];
                tree[k + half - 1] = tree[k + stride - 1];
                tree[k + stride - 1] += t;
                opsInRound++;
                workCount += 2;
            }
            logs.add(String.format("Down-Sweep step %d (stride %d): %d parallel node update(s)", logN - d, stride, opsInRound));
        }

        long[] exclusive = new long[origLen];
        long[] inclusive = new long[origLen];
        for (int i = 0; i < origLen; i++) {
            exclusive[i] = tree[i];
            inclusive[i] = tree[i] + input[i];
        }

        return new ScanResult(input, exclusive, inclusive, totalSum, workCount, spanCount, logs);
    }

    private static int nextPowerOfTwo(int n) {
        int power = 1;
        while (power < n) {
            power <<= 1;
        }
        return power;
    }
}
