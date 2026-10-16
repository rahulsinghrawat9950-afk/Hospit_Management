package com.smarthospital;

import com.smarthospital.m5_np.*;
import com.smarthospital.model.HospitalDatabase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Module5Test {

    @Test
    public void testSat3SatSolverSatisfiable() {
        List<Sat3SatSolver.Clause> clauses = HospitalDatabase.getDefaultTreatmentSatClauses();
        assertTrue(Sat3SatSolver.is3Sat(clauses), "Should be valid 3-SAT formula");

        Sat3SatSolver.SatResult res = Sat3SatSolver.solve(clauses, HospitalDatabase.getTreatmentVarNames());
        assertTrue(res.satisfiable, "Treatment protocol formula should be satisfiable");
        assertNotNull(res.namedAssignments);
        assertFalse(res.namedAssignments.isEmpty());
    }

    @Test
    public void testSat3SatSolverUnsatisfiable() {
        // (x1) and (-x1)
        List<Sat3SatSolver.Clause> unsatisfiable = Arrays.asList(
                new Sat3SatSolver.Clause(1),
                new Sat3SatSolver.Clause(-1)
        );
        Sat3SatSolver.SatResult res = Sat3SatSolver.solve(unsatisfiable, null);
        assertFalse(res.satisfiable, "Contradictory formula must be unsatisfiable");
    }

    @Test
    public void test3SatToCliqueReduction() {
        List<Sat3SatSolver.Clause> clauses = HospitalDatabase.getDefaultTreatmentSatClauses();
        SatToCliqueReduction.ReductionResult res = SatToCliqueReduction.reduceAndSolve(clauses);

        assertTrue(res.hasSizeKClique, "Satisfiable formula must yield a k-clique in reduction graph");
        assertEquals(clauses.size(), res.clique.size());
        assertFalse(res.extractedModel.isEmpty());
    }

    @Test
    public void testKarpChainReductions() {
        NpReductions.ConflictGraph g = HospitalDatabase.getDefaultHospitalConflictGraph();
        NpReductions.ReductionChainResult res = NpReductions.analyzeReductions(g);

        assertTrue(res.theoremVerified, "|MIS| + |MVC| must equal |V|");
        assertEquals(g.numVertices, res.independentSetSize + res.vertexCoverSize);
        assertTrue(res.cliqueSize >= 2);
    }

    @Test
    public void testVertexCover2Approximation() {
        NpReductions.ConflictGraph g = HospitalDatabase.getDefaultHospitalConflictGraph();
        VertexCoverApproximation.ApproxResult res = VertexCoverApproximation.compute2Approximation(g);

        assertTrue(res.coverSize > 0);
        assertTrue(res.theoreticalMaxRatio <= 2.0);
        if (res.actualRatio != null) {
            assertTrue(res.actualRatio <= 2.0, "Approximation ratio must not exceed 2.0");
        }
    }
}
