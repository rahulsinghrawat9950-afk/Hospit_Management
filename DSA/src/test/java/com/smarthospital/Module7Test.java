package com.smarthospital;

import com.smarthospital.m7_parallel.*;
import com.smarthospital.model.HospitalDatabase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Module7Test {

    @Test
    public void testBlellochScan() {
        long[] input = HospitalDatabase.getDefaultPatientDosageArray(); // [15, 20, 10, 35, 25, 40, 30, 50]
        BlellochScan.ScanResult res = BlellochScan.scan(input);

        assertEquals(input.length, res.exclusiveScan.length);
        assertEquals(input.length, res.inclusiveScan.length);

        // Verify prefix sums manually
        long running = 0;
        for (int i = 0; i < input.length; i++) {
            assertEquals(running, res.exclusiveScan[i], "Exclusive scan at " + i + " mismatch");
            running += input[i];
            assertEquals(running, res.inclusiveScan[i], "Inclusive scan at " + i + " mismatch");
        }
        assertEquals(running, res.totalSum);
        assertTrue(res.work > 0);
        assertTrue(res.span > 0);
    }

    @Test
    public void testParallelReduce() {
        double[] data = new double[1000];
        double expectedSum = 0;
        for (int i = 0; i < data.length; i++) {
            data[i] = i + 1;
            expectedSum += (i + 1);
        }

        ParallelReduce.ReduceResult sumRes = ParallelReduce.compute(data, ParallelReduce.Operation.SUM);
        assertEquals(expectedSum, sumRes.result, 1e-6);

        ParallelReduce.ReduceResult minRes = ParallelReduce.compute(data, ParallelReduce.Operation.MIN);
        assertEquals(1.0, minRes.result, 1e-6);

        ParallelReduce.ReduceResult maxRes = ParallelReduce.compute(data, ParallelReduce.Operation.MAX);
        assertEquals(1000.0, maxRes.result, 1e-6);

        double avg = ParallelReduce.computeAverage(data);
        assertEquals(expectedSum / 1000.0, avg, 1e-6);
    }

    @Test
    public void testBrentsTheoremAnalysis() {
        BrentsTheorem.BrentsAnalysisResult res = BrentsTheorem.modelHospitalTreeAggregation(1000);

        assertEquals(999, res.work);
        assertEquals(10, res.span); // ceil(log2(1000)) = 10
        assertTrue(res.parallelismDegree > 0);
        assertEquals(100, res.optimalProcessors);
        assertFalse(res.scalingCurve.isEmpty());

        for (BrentsTheorem.ProcessorMetric m : res.scalingCurve) {
            assertTrue(m.lowerBoundTime <= m.upperBoundTime, "Lower bound must be <= upper bound");
            assertTrue(m.efficiency <= 1.05, "Efficiency must be <= 1 (allowing small rounding)");
        }
    }
}
