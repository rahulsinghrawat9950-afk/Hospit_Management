package com.smarthospital.m6_randomized;

import java.time.Instant;
import java.util.*;

/**
 * Module 6 (CO6): Reservoir Sampling for Continuous Hospital Observations.
 * Uniformly samples k representative readings from unbounded or real-time ICU sensor streams
 * (heart rate, SpO2, blood pressure) where total stream length N is unknown in advance.
 */
public class ReservoirSampling {

    public static class Observation {
        public final String observationId;
        public final String patientId;
        public final String metric; // e.g., "HEART_RATE", "SPO2", "BLOOD_PRESSURE"
        public final double value;
        public final String unit;
        public final Instant timestamp;

        public Observation(String observationId, String patientId, String metric, double value, String unit) {
            this.observationId = observationId;
            this.patientId = patientId;
            this.metric = metric;
            this.value = value;
            this.unit = unit;
            this.timestamp = Instant.now();
        }

        @Override
        public String toString() {
            return String.format("[%s] Patient %s: %s = %.1f %s", observationId, patientId, metric, value, unit);
        }
    }

    public static class ReservoirStream<T> {
        private final int k;
        private final List<T> reservoir;
        private long totalCount;
        private final Random random;

        public ReservoirStream(int k) {
            this.k = k;
            this.reservoir = new ArrayList<>(k);
            this.totalCount = 0;
            this.random = new Random();
        }

        public synchronized void observe(T item) {
            totalCount++;
            if (reservoir.size() < k) {
                reservoir.add(item);
            } else {
                // Pick random index in [0, totalCount - 1]
                long r = (long) (random.nextDouble() * totalCount);
                if (r < k) {
                    reservoir.set((int) r, item);
                }
            }
        }

        public synchronized List<T> getSample() {
            return new ArrayList<>(reservoir);
        }

        public synchronized long getTotalObserved() {
            return totalCount;
        }

        public synchronized int getReservoirCapacity() {
            return k;
        }

        public synchronized double getSelectionProbability() {
            if (totalCount == 0) return 0.0;
            return totalCount <= k ? 1.0 : (double) k / totalCount;
        }
    }

    public static <T> List<T> sample(Iterable<T> stream, int k) {
        if (k <= 0) return Collections.emptyList();
        ReservoirStream<T> sampler = new ReservoirStream<>(k);
        for (T item : stream) {
            sampler.observe(item);
        }
        return sampler.getSample();
    }
}
