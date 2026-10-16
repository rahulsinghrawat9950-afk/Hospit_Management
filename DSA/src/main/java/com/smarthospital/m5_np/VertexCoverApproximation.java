package com.smarthospital.m5_np;

import java.util.*;

/**
 * Module 5 (CO5): Vertex-Cover 2-Approximation for Large Conflict Networks.
 * Employs maximal matching greedy edge selection to guarantee an approximation ratio <= 2.0
 * for large-scale hospital contagion tracking and doctor shift conflict resolution.
 */
public class VertexCoverApproximation {

    public static class ApproxResult {
        public final List<String> approximateCover;
        public final List<String> selectedMatchingEdges;
        public final int coverSize;
        public final int matchingSize;
        public final double theoreticalMaxRatio;
        public final Integer optimalCoverSize; // Optional if computed exactly
        public final Double actualRatio;
        public final String guaranteeReport;

        public ApproxResult(List<String> approximateCover, List<String> selectedMatchingEdges,
                            int coverSize, int matchingSize, double theoreticalMaxRatio,
                            Integer optimalCoverSize, Double actualRatio, String guaranteeReport) {
            this.approximateCover = approximateCover;
            this.selectedMatchingEdges = selectedMatchingEdges;
            this.coverSize = coverSize;
            this.matchingSize = matchingSize;
            this.theoreticalMaxRatio = theoreticalMaxRatio;
            this.optimalCoverSize = optimalCoverSize;
            this.actualRatio = actualRatio;
            this.guaranteeReport = guaranteeReport;
        }
    }

    public static ApproxResult compute2Approximation(NpReductions.ConflictGraph graph) {
        int n = graph.numVertices;
        boolean[] inCover = new boolean[n];
        List<String> matchingEdges = new ArrayList<>();
        int matchingCount = 0;

        // Copy adjacency so we can remove covered edges
        boolean[][] remaining = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(graph.adj[i], 0, remaining[i], 0, n);
        }

        for (int u = 0; u < n; u++) {
            for (int v = u + 1; v < n; v++) {
                if (remaining[u][v] && !inCover[u] && !inCover[v]) {
                    // Pick edge (u, v) into maximal matching
                    inCover[u] = true;
                    inCover[v] = true;
                    matchingCount++;
                    matchingEdges.add(graph.vertexNames.get(u) + " -- " + graph.vertexNames.get(v));

                    // Eliminate all incident edges to u and v
                    for (int k = 0; k < n; k++) {
                        remaining[u][k] = false;
                        remaining[k][u] = false;
                        remaining[v][k] = false;
                        remaining[k][v] = false;
                    }
                }
            }
        }

        List<String> coverNames = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (inCover[i]) {
                coverNames.add(graph.vertexNames.get(i));
            }
        }

        Integer optSize = null;
        Double actualRatio = null;

        // If graph is small enough (<= 25 vertices), compute exact optimal to compare
        if (n <= 25) {
            NpReductions.ReductionChainResult exact = NpReductions.analyzeReductions(graph);
            optSize = exact.vertexCoverSize;
            if (optSize > 0) {
                actualRatio = (double) coverNames.size() / optSize;
            } else {
                actualRatio = 1.0;
            }
        }

        String report = String.format(
                "2-Approximation Analysis:\n" +
                "  - Maximal Matching Size (|M|): %d\n" +
                "  - Approximate Vertex Cover Size (|C| = 2*|M|): %d\n" +
                "  - Theoretical Lower Bound (|C*| >= |M|): %d\n" +
                "  - Approximation Guarantee: |C| <= 2 * |C*| (Ratio <= 2.0)\n" +
                (optSize != null ? String.format("  - Exact Optimal Size (|C*|): %d (Actual Ratio: %.2f)", optSize, actualRatio) : "  - Graph too large for exact exponential check; 2.0 bound guaranteed."),
                matchingCount, coverNames.size(), matchingCount);

        return new ApproxResult(coverNames, matchingEdges, coverNames.size(), matchingCount,
                2.0, optSize, actualRatio, report);
    }
}
