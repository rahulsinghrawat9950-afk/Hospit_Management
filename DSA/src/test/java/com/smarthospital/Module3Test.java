package com.smarthospital;

import com.smarthospital.m3_dp.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Module3Test {

    @Test
    public void testLevenshtein() {
        assertEquals(1, LevenshteinDistance.compute("Aspirin", "Asprin"));
        assertEquals(0, LevenshteinDistance.compute("Ibuprofen", "Ibuprofen"));
    }

    @Test
    public void testDamerauLevenshtein() {
        // Transposition test: "hsopital" vs "hospital"
        assertEquals(1, DamerauLevenshtein.compute("hsopital", "hospital"));
    }

    @Test
    public void testSequenceAlignment() {
        NeedlemanWunsch.AlignmentResult nw = NeedlemanWunsch.align("GATTACA", "GCATGCU", 1, -1, -1);
        assertNotNull(nw);

        SmithWaterman.AlignmentResult sw = SmithWaterman.align("ACACTAGAT", "AGCACACAGA", 2, -1, -1);
        assertTrue(sw.maxScore > 0);
    }

    @Test
    public void testBitmaskDP() {
        int[] costs = {50, 100, 150};
        int[] values = {40, 90, 110};
        int val = BitmaskDP.optimizeDiagnosticTests(costs, values, 200);
        assertEquals(150, val);
    }
}
