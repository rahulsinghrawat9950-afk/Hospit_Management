package com.smarthospital.m5_np;

import java.util.*;

/**
 * Module 5 (CO5): Karp Polynomial-Time Reduction: 3-SAT → CLIQUE.
 * Transforms treatment constraint satisfaction problems into maximum clique discovery
 * over compatibility conflict graphs.
 */
public class SatToCliqueReduction {

    public static class CliqueNode {
        public final int id;
        public final int clauseIndex;
        public final int literal;
        public final String label;

        public CliqueNode(int id, int clauseIndex, int literal) {
            this.id = id;
            this.clauseIndex = clauseIndex;
            this.literal = literal;
            this.label = String.format("Clause %d: %s", clauseIndex + 1,
                    (literal > 0 ? "x" + literal : "¬x" + (-literal)));
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public static class ReductionGraph {
        public final int kClauses;
        public final List<CliqueNode> nodes;
        public final boolean[][] adjMatrix;

        public ReductionGraph(int kClauses, List<CliqueNode> nodes, boolean[][] adjMatrix) {
            this.kClauses = kClauses;
            this.nodes = nodes;
            this.adjMatrix = adjMatrix;
        }

        public int getNodeCount() {
            return nodes.size();
        }

        public int getEdgeCount() {
            int count = 0;
            for (int i = 0; i < nodes.size(); i++) {
                for (int j = i + 1; j < nodes.size(); j++) {
                    if (adjMatrix[i][j]) count++;
                }
            }
            return count;
        }
    }

    public static class ReductionResult {
        public final ReductionGraph graph;
        public final boolean hasSizeKClique;
        public final List<CliqueNode> clique;
        public final Map<Integer, Boolean> extractedModel;
        public final String explanation;

        public ReductionResult(ReductionGraph graph, boolean hasSizeKClique,
                               List<CliqueNode> clique, Map<Integer, Boolean> extractedModel,
                               String explanation) {
            this.graph = graph;
            this.hasSizeKClique = hasSizeKClique;
            this.clique = clique;
            this.extractedModel = extractedModel;
            this.explanation = explanation;
        }
    }

    public static ReductionResult reduceAndSolve(List<Sat3SatSolver.Clause> clauses) {
        int k = clauses.size();
        List<CliqueNode> nodes = new ArrayList<>();

        int id = 0;
        for (int i = 0; i < clauses.size(); i++) {
            Sat3SatSolver.Clause c = clauses.get(i);
            for (int lit : c.literals) {
                nodes.add(new CliqueNode(id++, i, lit));
            }
        }

        int totalNodes = nodes.size();
        boolean[][] adj = new boolean[totalNodes][totalNodes];

        // Connect nodes iff different clauses and not opposite literals
        for (int i = 0; i < totalNodes; i++) {
            for (int j = i + 1; j < totalNodes; j++) {
                CliqueNode u = nodes.get(i);
                CliqueNode v = nodes.get(j);
                if (u.clauseIndex != v.clauseIndex && u.literal != -v.literal) {
                    adj[i][j] = true;
                    adj[j][i] = true;
                }
            }
        }

        ReductionGraph graph = new ReductionGraph(k, nodes, adj);
        List<Integer> cliqueIndices = findCliqueOfSize(graph, k);

        if (cliqueIndices != null) {
            List<CliqueNode> clique = new ArrayList<>();
            Map<Integer, Boolean> model = new LinkedHashMap<>();
            for (int idx : cliqueIndices) {
                CliqueNode n = nodes.get(idx);
                clique.add(n);
                model.put(Math.abs(n.literal), n.literal > 0);
            }
            String expl = String.format("Reduction Successful: Found Clique of size k=%d across %d nodes and %d edges. " +
                            "This establishes that the 3-SAT medical constraints are SATISFIABLE.",
                    k, totalNodes, graph.getEdgeCount());
            return new ReductionResult(graph, true, clique, model, expl);
        } else {
            String expl = String.format("Reduction Complete: No Clique of size k=%d exists. " +
                            "Therefore, the 3-SAT medical constraint system is UNSATISFIABLE.",
                    k);
            return new ReductionResult(graph, false, Collections.emptyList(), Collections.emptyMap(), expl);
        }
    }

    private static List<Integer> findCliqueOfSize(ReductionGraph graph, int targetSize) {
        List<Integer> current = new ArrayList<>();
        return searchClique(graph, 0, current, targetSize);
    }

    private static List<Integer> searchClique(ReductionGraph graph, int startIdx,
                                              List<Integer> current, int targetSize) {
        if (current.size() == targetSize) {
            return new ArrayList<>(current);
        }

        int remaining = graph.nodes.size() - startIdx;
        if (current.size() + remaining < targetSize) {
            return null;
        }

        for (int i = startIdx; i < graph.nodes.size(); i++) {
            boolean canAdd = true;
            for (int prev : current) {
                if (!graph.adjMatrix[prev][i]) {
                    canAdd = false;
                    break;
                }
            }
            if (canAdd) {
                current.add(i);
                List<Integer> res = searchClique(graph, i + 1, current, targetSize);
                if (res != null) return res;
                current.remove(current.size() - 1);
            }
        }
        return null;
    }
}
