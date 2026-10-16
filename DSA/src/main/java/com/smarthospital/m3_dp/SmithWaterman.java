package com.smarthospital.m3_dp;

/**
 * Module 3 (CO3): Smith-Waterman Algorithm
 * Local alignment of biological sequences (identifying similar gene sub-regions).
 */
public class SmithWaterman {

    public static class AlignmentResult {
        public final int maxScore;
        public final String alignedSeq1;
        public final String alignedSeq2;

        public AlignmentResult(int maxScore, String alignedSeq1, String alignedSeq2) {
            this.maxScore = maxScore;
            this.alignedSeq1 = alignedSeq1;
            this.alignedSeq2 = alignedSeq2;
        }
    }

    public static AlignmentResult align(String seq1, String seq2, int matchScore, int mismatchPenalty, int gapPenalty) {
        int m = seq1.length();
        int n = seq2.length();
        int[][] dp = new int[m + 1][n + 1];

        int maxScore = 0;
        int maxI = 0, maxJ = 0;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int scoreDiag = dp[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchPenalty);
                int scoreUp = dp[i - 1][j] + gapPenalty;
                int scoreLeft = dp[i][j - 1] + gapPenalty;
                dp[i][j] = Math.max(0, Math.max(scoreDiag, Math.max(scoreUp, scoreLeft)));

                if (dp[i][j] > maxScore) {
                    maxScore = dp[i][j];
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        StringBuilder a1 = new StringBuilder();
        StringBuilder a2 = new StringBuilder();
        int i = maxI, j = maxJ;

        while (i > 0 && j > 0 && dp[i][j] > 0) {
            if (dp[i][j] == dp[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchPenalty)) {
                a1.append(seq1.charAt(i - 1));
                a2.append(seq2.charAt(j - 1));
                i--; j--;
            } else if (dp[i][j] == dp[i - 1][j] + gapPenalty) {
                a1.append(seq1.charAt(i - 1));
                a2.append('-');
                i--;
            } else {
                a1.append('-');
                a2.append(seq2.charAt(j - 1));
                j--;
            }
        }

        return new AlignmentResult(maxScore, a1.reverse().toString(), a2.reverse().toString());
    }
}
