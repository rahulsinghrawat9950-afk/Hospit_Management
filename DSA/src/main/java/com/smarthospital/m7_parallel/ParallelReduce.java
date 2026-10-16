package com.smarthospital.m7_parallel;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Module 7 (CO6): Parallel Reduce for Hospital Analytics.
 * Computes parallel divide-and-conquer tree reductions (SUM, MIN, MAX, AVERAGE)
 * across massive clinical datasets using Java ForkJoin framework.
 * Work O(n), Span O(log n).
 */
public class ParallelReduce {

    public enum Operation {
        SUM, MIN, MAX
    }

    public static class ReduceResult {
        public final Operation operation;
        public final double result;
        public final int dataSize;
        public final int parallelism;
        public final long parallelTimeNanos;
        public final long sequentialTimeNanos;
        public final double speedup;

        public ReduceResult(Operation operation, double result, int dataSize,
                            int parallelism, long parallelTimeNanos,
                            long sequentialTimeNanos, double speedup) {
            this.operation = operation;
            this.result = result;
            this.dataSize = dataSize;
            this.parallelism = parallelism;
            this.parallelTimeNanos = parallelTimeNanos;
            this.sequentialTimeNanos = sequentialTimeNanos;
            this.speedup = speedup;
        }

        @Override
        public String toString() {
            return String.format("[%s] Result: %.2f (N=%d, Parallelism=%d | Seq: %.3f ms, Par: %.3f ms, Speedup: %.2fx)",
                    operation, result, dataSize, parallelism,
                    sequentialTimeNanos / 1_000_000.0, parallelTimeNanos / 1_000_000.0, speedup);
        }
    }

    private static final int SEQUENTIAL_THRESHOLD = 500;
    private static final ForkJoinPool POOL = ForkJoinPool.commonPool();

    private static class ReduceTask extends RecursiveTask<Double> {
        private final double[] data;
        private final int start;
        private final int end;
        private final Operation op;

        public ReduceTask(double[] data, int start, int end, Operation op) {
            this.data = data;
            this.start = start;
            this.end = end;
            this.op = op;
        }

        @Override
        protected Double compute() {
            if (end - start <= SEQUENTIAL_THRESHOLD) {
                return sequentialCompute(data, start, end, op);
            }
            int mid = (start + end) / 2;
            ReduceTask left = new ReduceTask(data, start, mid, op);
            ReduceTask right = new ReduceTask(data, mid, end, op);
            left.fork();
            double rightVal = right.compute();
            double leftVal = left.join();
            return combine(leftVal, rightVal, op);
        }
    }

    public static ReduceResult compute(double[] data, Operation op) {
        if (data == null || data.length == 0) {
            return new ReduceResult(op, 0, 0, POOL.getParallelism(), 0, 0, 1.0);
        }

        // Sequential benchmark
        long seqStart = System.nanoTime();
        double seqVal = sequentialCompute(data, 0, data.length, op);
        long seqTime = System.nanoTime() - seqStart;

        // Parallel execution
        long parStart = System.nanoTime();
        double parVal = POOL.invoke(new ReduceTask(data, 0, data.length, op));
        long parTime = System.nanoTime() - parStart;

        double speedup = parTime > 0 ? (double) seqTime / parTime : 1.0;

        return new ReduceResult(op, parVal, data.length, POOL.getParallelism(), parTime, seqTime, speedup);
    }

    public static double computeAverage(double[] data) {
        if (data == null || data.length == 0) return 0.0;
        ReduceResult sumRes = compute(data, Operation.SUM);
        return sumRes.result / data.length;
    }

    private static double sequentialCompute(double[] data, int start, int end, Operation op) {
        double acc = op == Operation.MIN ? Double.POSITIVE_INFINITY :
                     op == Operation.MAX ? Double.NEGATIVE_INFINITY : 0.0;
        for (int i = start; i < end; i++) {
            acc = combine(acc, data[i], op);
        }
        return acc;
    }

    private static double combine(double a, double b, Operation op) {
        switch (op) {
            case MIN: return Math.min(a, b);
            case MAX: return Math.max(a, b);
            case SUM: return a + b;
            default: return a + b;
        }
    }
}
