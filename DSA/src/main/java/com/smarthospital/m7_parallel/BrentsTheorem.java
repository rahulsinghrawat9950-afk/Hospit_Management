package com.smarthospital.m7_parallel;

import java.util.*;

/**
 * Module 7 (CO6): Brent's Theorem for Parallel Processing Analysis.
 * Models work-span performance bounds in hospital computational workloads:
 * W / p <= T_p <= (W - T_inf) / p + T_inf.
 * Determines maximum parallel speedup, hardware processor efficiency, and saturation thresholds.
 */
public class BrentsTheorem {

    public static class ProcessorMetric {
        public final int processors;
        public final double lowerBoundTime;
        public final double upperBoundTime;
        public final double expectedSpeedup;
        public final double efficiency;

        public ProcessorMetric(int processors, double lowerBoundTime, double upperBoundTime,
                               double expectedSpeedup, double efficiency) {
            this.processors = processors;
            this.lowerBoundTime = lowerBoundTime;
            this.upperBoundTime = upperBoundTime;
            this.expectedSpeedup = expectedSpeedup;
            this.efficiency = efficiency;
        }

        @Override
        public String toString() {
            return String.format("p=%2d cores | Time: [%.2f, %.2f] | Speedup: %5.2fx | Efficiency: %5.1f%%",
                    processors, lowerBoundTime, upperBoundTime, expectedSpeedup, efficiency * 100);
        }
    }

    public static class BrentsAnalysisResult {
        public final long work; // T_1
        public final long span; // T_infinity
        public final double parallelismDegree; // W / T_infinity
        public final int optimalProcessors;
        public final List<ProcessorMetric> scalingCurve;
        public final String analyticalSummary;

        public BrentsAnalysisResult(long work, long span, double parallelismDegree,
                                    int optimalProcessors, List<ProcessorMetric> scalingCurve,
                                    String analyticalSummary) {
            this.work = work;
            this.span = span;
            this.parallelismDegree = parallelismDegree;
            this.optimalProcessors = optimalProcessors;
            this.scalingCurve = scalingCurve;
            this.analyticalSummary = analyticalSummary;
        }
    }

    /**
     * Analyzes parallel runtime bounds according to Brent's Theorem for a given Work W and Span T_inf.
     */
    public static BrentsAnalysisResult analyze(long work, long span, int[] processorCounts) {
        if (span <= 0) span = 1;
        if (work < span) work = span;

        double parallelism = (double) work / span;
        int optimalCores = (int) Math.ceil(parallelism);

        List<ProcessorMetric> curve = new ArrayList<>();
        for (int p : processorCounts) {
            if (p <= 0) continue;
            double lowerBound = Math.max((double) work / p, span);
            double upperBound = ((double) (work - span) / p) + span;
            double estTime = (lowerBound + upperBound) / 2.0;
            double speedup = (double) work / estTime;
            double efficiency = speedup / p;

            curve.add(new ProcessorMetric(p, lowerBound, upperBound, speedup, efficiency));
        }

        String summary = String.format(
                "Brent's Theorem Architectural Analysis:\n" +
                "  - Total Work (W = T1): %d operations\n" +
                "  - Critical Path Span (T∞): %d steps\n" +
                "  - Algorithmic Parallelism (W / T∞): %.2f\n" +
                "  - Recommended Processor Saturation Limit: %d cores\n" +
                "  - Core Formula: W/p <= T_p <= (W - T∞)/p + T∞",
                work, span, parallelism, optimalCores);

        return new BrentsAnalysisResult(work, span, parallelism, optimalCores, curve, summary);
    }

    /**
     * Convenience model for hospital tree reduction analytics of size N.
     * Work W = N - 1, Span = ceil(log2(N)).
     */
    public static BrentsAnalysisResult modelHospitalTreeAggregation(int nPatients) {
        long work = Math.max(1, nPatients - 1);
        long span = (long) Math.ceil(Math.log(nPatients) / Math.log(2));
        int[] cores = {1, 2, 4, 8, 16, 32, 64};
        return analyze(work, span, cores);
    }
}
