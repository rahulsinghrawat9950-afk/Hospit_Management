package com.smarthospital.m6_randomized;

import com.smarthospital.model.PatientRecord;

import java.util.*;

/**
 * Module 6 (CO6): Randomized QuickSort for Medical-Record Ranking.
 * Ranks emergency patient admissions and triage priority lists using randomized pivot selection.
 * Guarantees expected O(n log n) runtime and protects against adversarial worst-case O(n^2) ordering.
 */
public class RandomizedQuickSort {

    public static class SortMetrics {
        public long comparisons = 0;
        public long swaps = 0;
        public int maxRecursionDepth = 0;
        public long executionTimeNanos = 0;

        @Override
        public String toString() {
            return String.format("Comparisons: %d | Swaps: %d | Max Depth: %d | Time: %.3f ms",
                    comparisons, swaps, maxRecursionDepth, executionTimeNanos / 1_000_000.0);
        }
    }

    private static final Random RANDOM = new Random();

    /**
     * Sorts a list of PatientRecords by urgency score descending (highest priority first).
     */
    public static SortMetrics rankPatientsByUrgency(List<PatientRecord> patients) {
        SortMetrics metrics = new SortMetrics();
        long start = System.nanoTime();
        if (patients != null && patients.size() > 1) {
            randomizedQuickSort(patients, 0, patients.size() - 1,
                    (p1, p2) -> Double.compare(p2.getUrgencyScore(), p1.getUrgencyScore()),
                    metrics, 1);
        }
        metrics.executionTimeNanos = System.nanoTime() - start;
        return metrics;
    }

    /**
     * Generic randomized quicksort with custom comparator.
     */
    public static <T> SortMetrics sort(List<T> list, Comparator<T> comparator) {
        SortMetrics metrics = new SortMetrics();
        long start = System.nanoTime();
        if (list != null && list.size() > 1) {
            randomizedQuickSort(list, 0, list.size() - 1, comparator, metrics, 1);
        }
        metrics.executionTimeNanos = System.nanoTime() - start;
        return metrics;
    }

    private static <T> void randomizedQuickSort(List<T> list, int low, int high,
                                               Comparator<T> comparator, SortMetrics metrics, int depth) {
        if (depth > metrics.maxRecursionDepth) {
            metrics.maxRecursionDepth = depth;
        }

        if (low < high) {
            int pIndex = randomizedPartition(list, low, high, comparator, metrics);
            randomizedQuickSort(list, low, pIndex - 1, comparator, metrics, depth + 1);
            randomizedQuickSort(list, pIndex + 1, high, comparator, metrics, depth + 1);
        }
    }

    private static <T> int randomizedPartition(List<T> list, int low, int high,
                                               Comparator<T> comparator, SortMetrics metrics) {
        // Uniformly pick a random pivot index in [low, high]
        int randomPivotIdx = low + RANDOM.nextInt(high - low + 1);
        swap(list, randomPivotIdx, high, metrics);

        T pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            metrics.comparisons++;
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;
                swap(list, i, j, metrics);
            }
        }
        swap(list, i + 1, high, metrics);
        return i + 1;
    }

    private static <T> void swap(List<T> list, int i, int j, SortMetrics metrics) {
        if (i != j) {
            metrics.swaps++;
            T temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }
}
