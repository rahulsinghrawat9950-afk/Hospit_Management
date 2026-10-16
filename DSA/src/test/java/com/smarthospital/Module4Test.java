package com.smarthospital;

import com.smarthospital.m4_flow.*;
import com.smarthospital.model.HospitalDatabase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Module4Test {

    @Test
    public void testFordFulkersonMaxFlow() {
        FlowNetwork net = HospitalDatabase.createDefaultResourceNetwork();
        FordFulkerson.FlowResult res = FordFulkerson.computeMaxFlow(net, 0, 5);
        assertEquals(23, res.maxFlow, "Ford-Fulkerson max flow on standard network must be 23");
        assertFalse(res.augmentingPaths.isEmpty());
        assertFalse(res.allocatedRoutes.isEmpty());
    }

    @Test
    public void testEdmondsKarpMinCut() {
        FlowNetwork net = HospitalDatabase.createDefaultResourceNetwork();
        EdmondsKarp.EdmondsKarpResult res = EdmondsKarp.computeMaxFlow(net, 0, 5);
        assertEquals(23, res.maxFlow, "Edmonds-Karp max flow must match 23");
        assertFalse(res.minCutEdges.isEmpty(), "Min-cut critical bottlenecks must be detected");
        assertTrue(res.reachableNodesS.contains("Central Medical Depot [Source]"));
        assertTrue(res.unreachableNodesT.contains("Regional Critical ICU [Sink]"));
    }

    @Test
    public void testDinicAlgorithm() {
        FlowNetwork net = HospitalDatabase.createDefaultResourceNetwork();
        DinicAlgorithm.DinicResult res = DinicAlgorithm.computeMaxFlow(net, 0, 5);
        assertEquals(23, res.maxFlow, "Dinic algorithm max flow must match 23");
        assertTrue(res.bfsPhases > 0);
        assertFalse(res.dispatchedRoutes.isEmpty());
    }

    @Test
    public void testBipartiteMatching() {
        HospitalDatabase.BipartiteSampleData sample = HospitalDatabase.getDefaultDoctorAssignmentData();
        BipartiteMatching.MatchingResult res = BipartiteMatching.computeMatching(
                sample.doctors, sample.cases, sample.compatibility);

        assertTrue(res.matchCount >= 3, "Matching should assign at least 3 doctors");
        assertEquals(res.matchCount, res.matches.size());
    }

    @Test
    public void testKonigsTheoremDuality() {
        HospitalDatabase.BipartiteSampleData sample = HospitalDatabase.getDefaultDoctorAssignmentData();
        BipartiteMatching.MatchingResult matchRes = BipartiteMatching.computeMatching(
                sample.doctors, sample.cases, sample.compatibility);
        KonigsTheorem.KonigResult konig = KonigsTheorem.computeMinimumVertexCover(matchRes);

        assertTrue(konig.theoremVerified, "König's theorem duality |M| == |MVC| must hold");
        assertEquals(matchRes.matchCount, konig.minVertexCoverSize);
        assertFalse(konig.allCoverNodes.isEmpty());
    }
}
