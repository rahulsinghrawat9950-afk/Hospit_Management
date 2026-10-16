package com.smarthospital.m3_dp;

/**
 * Module 3 (CO3): Needleman-Wunsch Algorithm
 * Global alignment of biological sequences (DNA / protein sequences in patient genetics).
 */
public class NeedlemanWunsch {

    public static class AlignmentResult {
        public final int score;
        public final String alignedSeq1;
        public final String alignedSeq2;

        public AlignmentResult(int score, String alignedSeq1, String alignedSeq2) {
            this.score = score;
            this.alignedSeq1 = alignedSeq1;
            this.alignedSeq2 = alignedSeq2;
        }
    }

    public static AlignmentResult align(String seq1, String seq2, int matchScore, int mismatchPenalty, int gapPenalty) {
        int m = seq1.length();
        int n = seq2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i * gapPenalty;
        for (int j = 0; j <= n; j++) dp[0][j] = j * gapPenalty;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int scoreDiag = dp[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchPenalty);
                int scoreUp = dp[i - 1][j] + gapPenalty;
                int scoreLeft = dp[i][j - 1] + gapPenalty;
                dp[i][j] = Math.max(scoreDiag, Math.max(scoreUp, scoreLeft));
            }
        }

        StringBuilder a1 = new StringBuilder();
        StringBuilder a2 = new StringBuilder();
        int i = m, j = n;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? matchScore : mismatchPenalty)) {
                a1.append(seq1.charAt(i - 1));
                a2.append(seq2.charAt(j - 1));
                i--; j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + gapPenalty) {
                a1.append(seq1.charAt(i - 1));
                a2.append('-');
                i--;
            } else {
                a1.append('-');
                a2.append(seq2.charAt(j - 1));
                j--;
            }
        }

        return new AlignmentResult(dp[m][n], a1.reverse().toString(), a2.reverse().toString());
    }
}
