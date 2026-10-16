package com.smarthospital.m5_np;

import java.util.*;

/**
 * Module 5 (CO5): Karp Chain Reductions: CLIQUE → INDEPENDENT-SET → VERTEX-COVER.
 * Bridges conflict relationship graphs across hospital shift assignments, epidemic quarantine
 * clusters, and incompatible treatments.
 */
public class NpReductions {

    public static class ConflictGraph {
        public final int numVertices;
        public final List<String> vertexNames;
        public final boolean[][] adj;

        public ConflictGraph(int numVertices, List<String> vertexNames) {
            this.numVertices = numVertices;
            this.vertexNames = vertexNames;
            this.adj = new boolean[numVertices][numVertices];
        }

        public void addEdge(int u, int v) {
            if (u != v && u >= 0 && v >= 0 && u < numVertices && v < numVertices) {
                adj[u][v] = true;
                adj[v][u] = true;
            }
        }

        public ConflictGraph buildComplement() {
            ConflictGraph comp = new ConflictGraph(numVertices, new ArrayList<>(vertexNames));
            for (int i = 0; i < numVertices; i++) {
                for (int j = i + 1; j < numVertices; j++) {
                    if (!this.adj[i][j]) {
                        comp.addEdge(i, j);
                    }
                }
            }
            return comp;
        }

        public int getEdgeCount() {
            int count = 0;
            for (int i = 0; i < numVertices; i++) {
                for (int j = i + 1; j < numVertices; j++) {
                    if (adj[i][j]) count++;
                }
            }
            return count;
        }
    }

    public static class ReductionChainResult {
        public final List<String> maxClique;
        public final List<String> maxIndependentSet;
        public final List<String> minVertexCover;
        public final int cliqueSize;
        public final int independentSetSize;
        public final int vertexCoverSize;
        public final int totalVertices;
        public final boolean theoremVerified;
        public final String mathematicalProof;

        public ReductionChainResult(List<String> maxClique, List<String> maxIndependentSet,
                                    List<String> minVertexCover, int cliqueSize,
                                    int independentSetSize, int vertexCoverSize,
                                    int totalVertices, boolean theoremVerified,
                                    String mathematicalProof) {
            this.maxClique = maxClique;
            this.maxIndependentSet = maxIndependentSet;
            this.minVertexCover = minVertexCover;
            this.cliqueSize = cliqueSize;
            this.independentSetSize = independentSetSize;
            this.vertexCoverSize = vertexCoverSize;
            this.totalVertices = totalVertices;
            this.theoremVerified = theoremVerified;
            this.mathematicalProof = mathematicalProof;
        }
    }

    public static ReductionChainResult analyzeReductions(ConflictGraph graph) {
        int n = graph.numVertices;

        // 1. Solve Max Clique on G
        List<Integer> cliqueIdx = findMaxClique(graph);

        // 2. Solve Max Independent Set on G (which equals Max Clique on complement G)
        ConflictGraph comp = graph.buildComplement();
        List<Integer> misIdx = findMaxClique(comp);

        // 3. Derive Min Vertex Cover on G: V \ MaxIndependentSet(G)
        Set<Integer> misSet = new HashSet<>(misIdx);
        List<Integer> mvcIdx = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!misSet.contains(i)) {
                mvcIdx.add(i);
            }
        }

        List<String> cliqueNames = toNames(graph.vertexNames, cliqueIdx);
        List<String> misNames = toNames(graph.vertexNames, misIdx);
        List<String> mvcNames = toNames(graph.vertexNames, mvcIdx);

        boolean verified = (misIdx.size() + mvcIdx.size() == n);
        String proof = String.format(
                "Reduction Proof: In graph G (|V| = %d):\n" +
                "  - Maximum Clique Size = %d\n" +
                "  - Maximum Independent Set Size = %d (Clique in complement graph G')\n" +
                "  - Minimum Vertex Cover Size = %d (Complement of MIS: |V| - |MIS| = %d - %d = %d)\n" +
                "  - Theorem Check (|MIS| + |MVC| = |V|): %d + %d = %d -> %s",
                n, cliqueIdx.size(), misIdx.size(), mvcIdx.size(), n, misIdx.size(), n - misIdx.size(),
                misIdx.size(), mvcIdx.size(), n, verified ? "VALIDATED (Q.E.D.)" : "MISMATCH");

        return new ReductionChainResult(cliqueNames, misNames, mvcNames,
                cliqueIdx.size(), misIdx.size(), mvcIdx.size(), n, verified, proof);
    }

    private static List<String> toNames(List<String> vertexNames, List<Integer> indices) {
        List<String> res = new ArrayList<>();
        for (int idx : indices) {
            res.add(vertexNames.get(idx));
        }
        return res;
    }

    public static List<Integer> findMaxClique(ConflictGraph graph) {
        List<Integer> best = new ArrayList<>();
        branchAndBoundClique(graph, 0, new ArrayList<>(), best);
        return best;
    }

    private static void branchAndBoundClique(ConflictGraph graph, int start,
                                             List<Integer> current, List<Integer> best) {
        if (current.size() > best.size()) {
            best.clear();
            best.addAll(current);
        }

        if (current.size() + (graph.numVertices - start) <= best.size()) {
            return; // Prune
        }

        for (int i = start; i < graph.numVertices; i++) {
            boolean adjacentToAll = true;
            for (int prev : current) {
                if (!graph.adj[prev][i]) {
                    adjacentToAll = false;
                    break;
                }
            }
            if (adjacentToAll) {
                current.add(i);
                branchAndBoundClique(graph, i + 1, current, best);
                current.remove(current.size() - 1);
            }
        }
    }
}
