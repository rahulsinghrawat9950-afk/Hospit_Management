package com.smarthospital.controller;

import com.smarthospital.m1_strings.*;
import com.smarthospital.m2_suffix.*;
import com.smarthospital.m3_dp.*;
import com.smarthospital.m4_flow.*;
import com.smarthospital.m5_np.*;
import com.smarthospital.m6_randomized.*;
import com.smarthospital.m7_parallel.*;
import com.smarthospital.model.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.*;

@RestController
@RequestMapping("/api")
public class HospitalRestController {

    // =========================================================================
    // MODULE 1: Strings (CO1)
    // =========================================================================
    @GetMapping("/search/kmp")
    public Map<String, Object> searchKmp(@RequestParam String text, @RequestParam String pattern) {
        List<Integer> matches = KmpSearch.search(text, pattern);
        Map<String, Object> response = new HashMap<>();
        response.put("text", text);
        response.put("pattern", pattern);
        response.put("matches", matches);
        response.put("matchCount", matches.size());
        return response;
    }

    @GetMapping("/search/aho-corasick")
    public Map<String, Object> searchAhoCorasick(@RequestParam String text, @RequestParam List<String> keywords) {
        AhoCorasick ac = new AhoCorasick(keywords);
        List<AhoCorasick.MatchResult> results = ac.searchIn(text);
        Map<String, Object> response = new HashMap<>();
        response.put("text", text);
        response.put("keywords", keywords);
        response.put("results", results);
        return response;
    }

    // =========================================================================
    // MODULE 2: Suffix Structures (CO2)
    // =========================================================================
    @GetMapping("/suffix/lcp")
    public Map<String, Object> findLcp(@RequestParam String text) {
        SuffixArray sa = new SuffixArray(text);
        int[] lcp = KasaiLCP.buildLCP(text, sa.getSuffixArray());
        String phrase = KasaiLCP.findLongestRepeatedMedicalPhrase(text, sa.getSuffixArray(), lcp);
        Map<String, Object> response = new HashMap<>();
        response.put("text", text);
        response.put("longestRepeatedPhrase", phrase);
        return response;
    }

    // =========================================================================
    // MODULE 3: Dynamic Programming (CO3)
    // =========================================================================
    @GetMapping("/typo/levenshtein")
    public Map<String, Object> checkLevenshtein(@RequestParam String term1, @RequestParam String term2) {
        int dist = LevenshteinDistance.compute(term1, term2);
        int damDist = DamerauLevenshtein.compute(term1, term2);
        Map<String, Object> response = new HashMap<>();
        response.put("term1", term1);
        response.put("term2", term2);
        response.put("levenshteinDistance", dist);
        response.put("damerauLevenshteinDistance", damDist);
        return response;
    }

    @GetMapping("/dna/align")
    public Map<String, Object> alignDna(@RequestParam String seq1, @RequestParam String seq2) {
        NeedlemanWunsch.AlignmentResult nw = NeedlemanWunsch.align(seq1, seq2, 1, -1, -2);
        SmithWaterman.AlignmentResult sw = SmithWaterman.align(seq1, seq2, 2, -1, -1);
        Map<String, Object> response = new HashMap<>();
        response.put("globalAlignmentScore", nw.score);
        response.put("globalAlignedSeq1", nw.alignedSeq1);
        response.put("globalAlignedSeq2", nw.alignedSeq2);
        response.put("localAlignmentScore", sw.maxScore);
        return response;
    }

    @GetMapping("/budget/bitmask")
    public Map<String, Object> optimizeTests(@RequestParam(defaultValue = "100,200,150") String costs,
                                             @RequestParam(defaultValue = "80,150,120") String benefits,
                                             @RequestParam(defaultValue = "300") int budget) {
        String[] cParts = costs.split(",");
        String[] bParts = benefits.split(",");
        int[] c = new int[cParts.length];
        int[] b = new int[bParts.length];
        for (int i = 0; i < cParts.length; i++) c[i] = Integer.parseInt(cParts[i].trim());
        for (int i = 0; i < bParts.length; i++) b[i] = Integer.parseInt(bParts[i].trim());

        int maxBenefit = BitmaskDP.optimizeDiagnosticTests(c, b, budget);
        Map<String, Object> response = new HashMap<>();
        response.put("budget", budget);
        response.put("maxBenefit", maxBenefit);
        return response;
    }

    // =========================================================================
    // MODULE 4: Network Flow (CO4)
    // =========================================================================
    @GetMapping("/flow/ford-fulkerson")
    public Map<String, Object> getFordFulkerson() {
        FlowNetwork net = HospitalDatabase.createDefaultResourceNetwork();
        FordFulkerson.FlowResult res = FordFulkerson.computeMaxFlow(net, 0, 5);
        Map<String, Object> response = new HashMap<>();
        response.put("algorithm", "Ford-Fulkerson");
        response.put("maxFlowUnits", res.maxFlow);
        response.put("augmentingPaths", res.augmentingPaths);
        response.put("allocatedRoutes", res.allocatedRoutes);
        return response;
    }

    @GetMapping("/flow/edmonds-karp")
    public Map<String, Object> getEdmondsKarp() {
        FlowNetwork net = HospitalDatabase.createDefaultResourceNetwork();
        EdmondsKarp.EdmondsKarpResult res = EdmondsKarp.computeMaxFlow(net, 0, 5);
        Map<String, Object> response = new HashMap<>();
        response.put("algorithm", "Edmonds-Karp (BFS Shortest Augmenting Path)");
        response.put("maxFlowUnits", res.maxFlow);
        response.put("minCutEdges", res.minCutEdges);
        response.put("sourcePartitionS", res.reachableNodesS);
        response.put("sinkPartitionT", res.unreachableNodesT);
        response.put("activeRoutes", res.activeRoutes);
        return response;
    }

    @GetMapping("/flow/dinic")
    public Map<String, Object> getDinic() {
        FlowNetwork net = HospitalDatabase.createDefaultResourceNetwork();
        DinicAlgorithm.DinicResult res = DinicAlgorithm.computeMaxFlow(net, 0, 5);
        Map<String, Object> response = new HashMap<>();
        response.put("algorithm", "Dinic Algorithm (Level Graph & Blocking Flow)");
        response.put("maxFlowUnits", res.maxFlow);
        response.put("bfsPhases", res.bfsPhases);
        response.put("routes", res.dispatchedRoutes);
        return response;
    }

    @GetMapping("/flow/bipartite-matching")
    public Map<String, Object> getBipartiteMatching() {
        HospitalDatabase.BipartiteSampleData sample = HospitalDatabase.getDefaultDoctorAssignmentData();
        BipartiteMatching.MatchingResult res = BipartiteMatching.computeMatching(
                sample.doctors, sample.cases, sample.compatibility);
        Map<String, Object> response = new HashMap<>();
        response.put("algorithm", "Maximum Bipartite Matching");
        response.put("matchedCasesCount", res.matchCount);
        response.put("assignments", res.matches.stream().map(Object::toString).toList());
        response.put("unassignedDoctors", res.unassignedDoctors);
        response.put("unassignedCases", res.unassignedCases);
        return response;
    }

    @GetMapping("/flow/konig")
    public Map<String, Object> getKonig() {
        HospitalDatabase.BipartiteSampleData sample = HospitalDatabase.getDefaultDoctorAssignmentData();
        BipartiteMatching.MatchingResult matchRes = BipartiteMatching.computeMatching(
                sample.doctors, sample.cases, sample.compatibility);
        KonigsTheorem.KonigResult konig = KonigsTheorem.computeMinimumVertexCover(matchRes);
        Map<String, Object> response = new HashMap<>();
        response.put("algorithm", "König's Theorem");
        response.put("maxMatchingSize", konig.maxMatchingSize);
        response.put("minVertexCoverSize", konig.minVertexCoverSize);
        response.put("theoremVerified", konig.theoremVerified);
        response.put("minimumVertexCoverNodes", konig.allCoverNodes);
        response.put("explanation", konig.explanation);
        return response;
    }

    // =========================================================================
    // MODULE 5: NP-Completeness & Approximation (CO5)
    // =========================================================================
    @GetMapping("/np/sat")
    public Map<String, Object> solve3Sat() {
        List<Sat3SatSolver.Clause> clauses = HospitalDatabase.getDefaultTreatmentSatClauses();
        Map<Integer, String> names = HospitalDatabase.getTreatmentVarNames();
        Sat3SatSolver.SatResult res = Sat3SatSolver.solve(clauses, names);
        Map<String, Object> response = new HashMap<>();
        response.put("algorithm", "DPLL 3-SAT Solver");
        response.put("formula", res.formulaString);
        response.put("satisfiable", res.satisfiable);
        response.put("treatmentAssignments", res.namedAssignments);
        return response;
    }

    @GetMapping("/np/clique-reduction")
    public Map<String, Object> reduceSatToClique() {
        List<Sat3SatSolver.Clause> clauses = HospitalDatabase.getDefaultTreatmentSatClauses();
        SatToCliqueReduction.ReductionResult res = SatToCliqueReduction.reduceAndSolve(clauses);
        Map<String, Object> response = new HashMap<>();
        response.put("reduction", "3-SAT -> CLIQUE");
        response.put("hasSizeKClique", res.hasSizeKClique);
        response.put("cliqueNodes", res.clique.stream().map(n -> n.label).toList());
        response.put("explanation", res.explanation);
        return response;
    }

    @GetMapping("/np/karp-reductions")
    public Map<String, Object> getKarpReductions() {
        NpReductions.ConflictGraph g = HospitalDatabase.getDefaultHospitalConflictGraph();
        NpReductions.ReductionChainResult res = NpReductions.analyzeReductions(g);
        Map<String, Object> response = new HashMap<>();
        response.put("chain", "CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER");
        response.put("maxClique", res.maxClique);
        response.put("maxIndependentSet", res.maxIndependentSet);
        response.put("minVertexCover", res.minVertexCover);
        response.put("theoremVerified", res.theoremVerified);
        response.put("proof", res.mathematicalProof);
        return response;
    }

    @GetMapping("/np/vertex-cover-approx")
    public Map<String, Object> getVertexCoverApprox() {
        NpReductions.ConflictGraph g = HospitalDatabase.getDefaultHospitalConflictGraph();
        VertexCoverApproximation.ApproxResult res = VertexCoverApproximation.compute2Approximation(g);
        Map<String, Object> response = new HashMap<>();
        response.put("algorithm", "Vertex Cover 2-Approximation (Maximal Matching)");
        response.put("approximateCover", res.approximateCover);
        response.put("selectedEdges", res.selectedMatchingEdges);
        response.put("theoreticalMaxRatio", res.theoreticalMaxRatio);
        response.put("actualRatio", res.actualRatio);
        response.put("report", res.guaranteeReport);
        return response;
    }

    // =========================================================================
    // MODULE 6: Randomized Algorithms (CO6)
    // =========================================================================
    @GetMapping("/randomized/quicksort")
    public Map<String, Object> rankPatients() {
        List<PatientRecord> patients = new ArrayList<>(HospitalDatabase.getPatients());
        RandomizedQuickSort.SortMetrics metrics = RandomizedQuickSort.rankPatientsByUrgency(patients);
        Map<String, Object> response = new HashMap<>();
        response.put("algorithm", "Randomized QuickSort");
        response.put("sortedPatients", patients.stream().map(p -> p.getName() + " (Urgency: " + p.getUrgencyScore() + ")").toList());
        response.put("metrics", metrics.toString());
        return response;
    }

    @GetMapping("/randomized/reservoir-sample")
    public Map<String, Object> sampleObservations(@RequestParam(defaultValue = "3") int k) {
        List<ReservoirSampling.Observation> stream = HospitalDatabase.getDefaultContinuousObservations();
        List<ReservoirSampling.Observation> sample = ReservoirSampling.sample(stream, k);
        Map<String, Object> response = new HashMap<>();
        response.put("algorithm", "Reservoir Sampling (Algorithm R)");
        response.put("totalStreamObserved", stream.size());
        response.put("sampleSizeK", k);
        response.put("sample", sample.stream().map(Object::toString).toList());
        response.put("selectionProbability", (double) k / stream.size());
        return response;
    }

    @GetMapping("/randomized/miller-rabin")
    public Map<String, Object> testMillerRabin(@RequestParam(defaultValue = "1000000007") String number,
                                               @RequestParam(defaultValue = "20") int rounds) {
        BigInteger n = new BigInteger(number);
        MillerRabin.PrimalityResult res = MillerRabin.testPrimality(n, rounds);
        BigInteger secureToken = MillerRabin.generateSecurePrime(64, rounds);
        Map<String, Object> response = new HashMap<>();
        response.put("testedNumber", n.toString());
        response.put("isProbablePrime", res.isProbablePrime);
        response.put("roundsConducted", res.roundsConducted);
        response.put("maxErrorProbability", res.errorProbability);
        response.put("generatedCryptographicPrimeToken", secureToken.toString());
        return response;
    }

    // =========================================================================
    // MODULE 7: Parallel Algorithms & Analytics Engine (CO6)
    // =========================================================================
    @GetMapping("/parallel/blelloch-scan")
    public Map<String, Object> getBlellochScan() {
        long[] doses = HospitalDatabase.getDefaultPatientDosageArray();
        BlellochScan.ScanResult res = BlellochScan.scan(doses);
        Map<String, Object> response = new HashMap<>();
        response.put("algorithm", "Blelloch Parallel Work-Efficient Scan");
        response.put("inputDoses", doses);
        response.put("exclusivePrefixScan", res.exclusiveScan);
        response.put("inclusivePrefixScan", res.inclusiveScan);
        response.put("totalDosageSum", res.totalSum);
        response.put("parallelWork", res.work);
        response.put("parallelSpan", res.span);
        response.put("treeLogs", res.phaseLogs);
        return response;
    }

    @GetMapping("/parallel/reduce")
    public Map<String, Object> getParallelReduce(@RequestParam(defaultValue = "2000") int size) {
        double[] data = new double[size];
        for (int i = 0; i < size; i++) {
            data[i] = 70.0 + (i % 50) * 1.5;
        }
        ParallelReduce.ReduceResult sumRes = ParallelReduce.compute(data, ParallelReduce.Operation.SUM);
        ParallelReduce.ReduceResult minRes = ParallelReduce.compute(data, ParallelReduce.Operation.MIN);
        ParallelReduce.ReduceResult maxRes = ParallelReduce.compute(data, ParallelReduce.Operation.MAX);
        double avg = ParallelReduce.computeAverage(data);

        Map<String, Object> response = new HashMap<>();
        response.put("algorithm", "Parallel Reduce (ForkJoin Framework)");
        response.put("dataSize", size);
        response.put("sumResult", sumRes.result);
        response.put("minResult", minRes.result);
        response.put("maxResult", maxRes.result);
        response.put("averageResult", avg);
        response.put("parallelSpeedupFactor", sumRes.speedup);
        return response;
    }

    @GetMapping("/parallel/brents-theorem")
    public Map<String, Object> getBrentsAnalysis(@RequestParam(defaultValue = "10000") int records) {
        BrentsTheorem.BrentsAnalysisResult res = BrentsTheorem.modelHospitalTreeAggregation(records);
        Map<String, Object> response = new HashMap<>();
        response.put("model", "Brent's Theorem Work-Span Performance Model");
        response.put("totalWorkT1", res.work);
        response.put("criticalPathSpanTInf", res.span);
        response.put("algorithmicParallelism", res.parallelismDegree);
        response.put("recommendedOptimalCores", res.optimalProcessors);
        response.put("scalingCurve", res.scalingCurve.stream().map(Object::toString).toList());
        response.put("summary", res.analyticalSummary);
        return response;
    }
}
