package com.smarthospital.m6_randomized;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

/**
 * Module 6 (CO6): Miller-Rabin Primality Test for Large Numerical Processing.
 * Validates large prime numbers for HIPAA-compliant cryptographic medical communications,
 * patient identity pseudonymization, and clinical trial tokenization.
 */
public class MillerRabin {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static class PrimalityResult {
        public final BigInteger number;
        public final boolean isProbablePrime;
        public final int roundsConducted;
        public final double errorProbability;
        public final String witnessInfo;

        public PrimalityResult(BigInteger number, boolean isProbablePrime, int roundsConducted,
                               double errorProbability, String witnessInfo) {
            this.number = number;
            this.isProbablePrime = isProbablePrime;
            this.roundsConducted = roundsConducted;
            this.errorProbability = errorProbability;
            this.witnessInfo = witnessInfo;
        }

        @Override
        public String toString() {
            return String.format("%s is %s (Rounds: %d, Max Error Prob: %.2e | %s)",
                    number, isProbablePrime ? "PROBABLE PRIME" : "COMPOSITE",
                    roundsConducted, errorProbability, witnessInfo);
        }
    }

    /**
     * Miller-Rabin test for BigInteger with k iterations.
     */
    public static PrimalityResult testPrimality(BigInteger n, int k) {
        if (n == null) return new PrimalityResult(BigInteger.ZERO, false, 0, 1.0, "Null input");

        // Small base cases
        if (n.compareTo(BigInteger.ONE) <= 0) {
            return new PrimalityResult(n, false, 0, 0.0, "Numbers <= 1 are not prime");
        }
        if (n.equals(BigInteger.TWO) || n.equals(BigInteger.valueOf(3))) {
            return new PrimalityResult(n, true, 0, 0.0, "Small prime base case");
        }
        if (!n.testBit(0)) {
            return new PrimalityResult(n, false, 0, 0.0, "Even number > 2 is composite");
        }

        // Factor n - 1 = 2^s * d with d odd
        BigInteger nMinusOne = n.subtract(BigInteger.ONE);
        BigInteger d = nMinusOne;
        int s = 0;
        while (!d.testBit(0)) {
            d = d.shiftRight(1);
            s++;
        }

        // Conduct k rounds with random bases a in [2, n - 2]
        for (int round = 1; round <= k; round++) {
            BigInteger a = getRandomBase(n);
            BigInteger x = a.modPow(d, n);

            if (x.equals(BigInteger.ONE) || x.equals(nMinusOne)) {
                continue;
            }

            boolean composite = true;
            for (int r = 1; r < s; r++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(nMinusOne)) {
                    composite = false;
                    break;
                }
            }

            if (composite) {
                return new PrimalityResult(n, false, round, 0.0,
                        "Composite detected with witness base a = " + a);
            }
        }

        double maxErrorProb = Math.pow(0.25, k);
        return new PrimalityResult(n, true, k, maxErrorProb,
                "Passed all " + k + " randomized Miller-Rabin rounds");
    }

    /**
     * Fast test for standard 64-bit primitive long.
     */
    public static boolean isPrime(long n, int k) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0) return false;
        return testPrimality(BigInteger.valueOf(n), k).isProbablePrime;
    }

    /**
     * Generates a secure prime token for patient record cryptography.
     */
    public static BigInteger generateSecurePrime(int bitLength, int rounds) {
        while (true) {
            BigInteger candidate = new BigInteger(bitLength, RANDOM);
            candidate = candidate.setBit(0); // Ensure odd
            candidate = candidate.setBit(bitLength - 1); // Ensure exact bitLength
            if (testPrimality(candidate, rounds).isProbablePrime) {
                return candidate;
            }
        }
    }

    private static BigInteger getRandomBase(BigInteger n) {
        BigInteger upper = n.subtract(BigInteger.valueOf(2)); // n - 2
        BigInteger lower = BigInteger.TWO;
        while (true) {
            BigInteger r = new BigInteger(n.bitLength(), RANDOM);
            if (r.compareTo(lower) >= 0 && r.compareTo(upper) <= 0) {
                return r;
            }
        }
    }
}
