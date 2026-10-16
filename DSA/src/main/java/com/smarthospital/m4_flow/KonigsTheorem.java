package com.smarthospital.m4_flow;

import java.util.*;

/**
 * Module 4 (CO4): König's Theorem for Conflict Coverage.
 * Proves and computes: In any bipartite graph, Maximum Matching Size == Minimum Vertex Cover Size.
 * Identifies the minimum set of critical hospital agents/wards that covers all operational conflicts.
 */
public class KonigsTheorem {

    public static class KonigResult {
        public final int maxMatchingSize;
        public final int minVertexCoverSize;
        public final List<String> coverDoctors;
        public final List<String> coverCases;
        public final List<String> allCoverNodes;
        public final boolean theoremVerified;
        public final String explanation;

        public KonigResult(int maxMatchingSize, int minVertexCoverSize,
                           List<String> coverDoctors, List<String> coverCases,
                           List<String> allCoverNodes, boolean theoremVerified,
                           String explanation) {
            this.maxMatchingSize = maxMatchingSize;
            this.minVertexCoverSize = minVertexCoverSize;
            this.coverDoctors = coverDoctors;
            this.coverCases = coverCases;
            this.allCoverNodes = allCoverNodes;
            this.theoremVerified = theoremVerified;
            this.explanation = explanation;
        }
    }

    public static KonigResult computeMinimumVertexCover(BipartiteMatching.MatchingResult matching) {
        int nL = matching.doctorNames.size();
        int nR = matching.caseNames.size();

        // Step 1: Identify unmatched vertices in L (Doctors)
        boolean[] inZ_L = new boolean[nL];
        boolean[] inZ_R = new boolean[nR];
        Queue<Integer> queueL = new LinkedList<>();

        for (int d = 0; d < nL; d++) {
            if (matching.doctorToCase[d] == -1) {
                inZ_L[d] = true;
                queueL.add(d);
            }
        }

        // Step 2: Alternating BFS
        // Unmatched edges traverse L -> R
        // Matched edges traverse R -> L
        while (!queueL.isEmpty()) {
            int d = queueL.poll();
            // Traverse edges d -> c that are NOT in the matching
            for (int c : matching.adjacency.get(d)) {
                if (matching.doctorToCase[d] != c && !inZ_R[c]) {
                    inZ_R[c] = true;
                    // From c, we can follow the matched edge to caseToDoctor[c]
                    int matchedDoc = matching.caseToDoctor[c];
                    if (matchedDoc != -1 && !inZ_L[matchedDoc]) {
                        inZ_L[matchedDoc] = true;
                        queueL.add(matchedDoc);
                    }
                }
            }
        }

        // Step 3: Minimum Vertex Cover = (L \ Z) U (R ∩ Z)
        List<String> coverDoctors = new ArrayList<>();
        List<String> coverCases = new ArrayList<>();
        List<String> allCoverNodes = new ArrayList<>();

        for (int d = 0; d < nL; d++) {
            if (!inZ_L[d]) {
                String name = matching.doctorNames.get(d);
                coverDoctors.add(name);
                allCoverNodes.add("[Doctor] " + name);
            }
        }

        for (int c = 0; c < nR; c++) {
            if (inZ_R[c]) {
                String name = matching.caseNames.get(c);
                coverCases.add(name);
                allCoverNodes.add("[Case] " + name);
            }
        }

        int coverSize = coverDoctors.size() + coverCases.size();
        boolean verified = (coverSize == matching.matchCount);

        String explanation = String.format(
                "König's Duality: Maximum Bipartite Matching (|M| = %d) == Minimum Vertex Cover (|C| = %d). " +
                "Any operational conflict between doctors and emergency cases is covered by this minimal set.",
                matching.matchCount, coverSize);

        return new KonigResult(matching.matchCount, coverSize, coverDoctors, coverCases, allCoverNodes, verified, explanation);
    }
}
