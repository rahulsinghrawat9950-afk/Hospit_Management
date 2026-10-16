package com.smarthospital.m4_flow;

import java.util.*;

/**
 * Module 4 (CO4): Maximum Bipartite Matching for Doctor-Patient / Doctor-Case Assignment.
 * Optimally assigns available medical specialists to pending emergency patients/cases.
 */
public class BipartiteMatching {

    public static class MatchPair {
        public final String doctor;
        public final String patientCase;
        public final int doctorIdx;
        public final int caseIdx;

        public MatchPair(String doctor, String patientCase, int doctorIdx, int caseIdx) {
            this.doctor = doctor;
            this.patientCase = patientCase;
            this.doctorIdx = doctorIdx;
            this.caseIdx = caseIdx;
        }

        @Override
        public String toString() {
            return doctor + " <=> " + patientCase;
        }
    }

    public static class MatchingResult {
        public final int matchCount;
        public final List<MatchPair> matches;
        public final List<String> unassignedDoctors;
        public final List<String> unassignedCases;
        public final int[] doctorToCase;
        public final int[] caseToDoctor;
        public final List<String> doctorNames;
        public final List<String> caseNames;
        public final List<List<Integer>> adjacency;

        public MatchingResult(int matchCount, List<MatchPair> matches,
                              List<String> unassignedDoctors, List<String> unassignedCases,
                              int[] doctorToCase, int[] caseToDoctor,
                              List<String> doctorNames, List<String> caseNames,
                              List<List<Integer>> adjacency) {
            this.matchCount = matchCount;
            this.matches = matches;
            this.unassignedDoctors = unassignedDoctors;
            this.unassignedCases = unassignedCases;
            this.doctorToCase = doctorToCase;
            this.caseToDoctor = caseToDoctor;
            this.doctorNames = doctorNames;
            this.caseNames = caseNames;
            this.adjacency = adjacency;
        }
    }

    public static MatchingResult computeMatching(List<String> doctors, List<String> cases,
                                                  List<List<Integer>> doctorCaseCompatibility) {
        int numDoctors = doctors.size();
        int numCases = cases.size();

        int[] caseToDoctor = new int[numCases];
        Arrays.fill(caseToDoctor, -1);
        int[] doctorToCase = new int[numDoctors];
        Arrays.fill(doctorToCase, -1);

        int matchCount = 0;
        for (int d = 0; d < numDoctors; d++) {
            boolean[] visitedCases = new boolean[numCases];
            if (tryAugment(d, doctorCaseCompatibility, visitedCases, caseToDoctor)) {
                matchCount++;
            }
        }

        for (int c = 0; c < numCases; c++) {
            if (caseToDoctor[c] != -1) {
                doctorToCase[caseToDoctor[c]] = c;
            }
        }

        List<MatchPair> pairs = new ArrayList<>();
        List<String> unassignedDoctors = new ArrayList<>();
        List<String> unassignedCases = new ArrayList<>();

        for (int d = 0; d < numDoctors; d++) {
            if (doctorToCase[d] != -1) {
                pairs.add(new MatchPair(doctors.get(d), cases.get(doctorToCase[d]), d, doctorToCase[d]));
            } else {
                unassignedDoctors.add(doctors.get(d));
            }
        }

        for (int c = 0; c < numCases; c++) {
            if (caseToDoctor[c] == -1) {
                unassignedCases.add(cases.get(c));
            }
        }

        return new MatchingResult(matchCount, pairs, unassignedDoctors, unassignedCases,
                doctorToCase, caseToDoctor, doctors, cases, doctorCaseCompatibility);
    }

    private static boolean tryAugment(int doctor, List<List<Integer>> compatibility,
                                      boolean[] visitedCases, int[] caseToDoctor) {
        for (int c : compatibility.get(doctor)) {
            if (!visitedCases[c]) {
                visitedCases[c] = true;
                if (caseToDoctor[c] < 0 || tryAugment(caseToDoctor[c], compatibility, visitedCases, caseToDoctor)) {
                    caseToDoctor[c] = doctor;
                    return true;
                }
            }
        }
        return false;
    }
}
