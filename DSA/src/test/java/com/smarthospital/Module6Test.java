package com.smarthospital;

import com.smarthospital.m6_randomized.*;
import com.smarthospital.model.HospitalDatabase;
import com.smarthospital.model.PatientRecord;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Module6Test {

    @Test
    public void testRandomizedQuickSort() {
        List<PatientRecord> patients = new ArrayList<>(HospitalDatabase.getPatients());
        RandomizedQuickSort.SortMetrics metrics = RandomizedQuickSort.rankPatientsByUrgency(patients);

        assertNotNull(metrics);
        assertTrue(patients.size() >= 2);
        // Verify descending order of urgency
        for (int i = 0; i < patients.size() - 1; i++) {
            assertTrue(patients.get(i).getUrgencyScore() >= patients.get(i + 1).getUrgencyScore(),
                    "Patients must be sorted descending by urgency");
        }
    }

    @Test
    public void testReservoirSampling() {
        List<ReservoirSampling.Observation> observations = HospitalDatabase.getDefaultContinuousObservations();
        int k = 4;
        List<ReservoirSampling.Observation> sample = ReservoirSampling.sample(observations, k);

        assertEquals(k, sample.size(), "Sample size must equal reservoir size k");
        for (ReservoirSampling.Observation obs : sample) {
            assertTrue(observations.contains(obs), "Sampled element must come from the stream");
        }
    }

    @Test
    public void testMillerRabinPrimality() {
        // Known prime numbers
        assertTrue(MillerRabin.isPrime(1000000007L, 15));
        assertTrue(MillerRabin.isPrime(2147483647L, 15)); // Mersenne prime 2^31 - 1
        assertTrue(MillerRabin.isPrime(13L, 10));

        // Known composite numbers
        assertFalse(MillerRabin.isPrime(1000000005L, 15));
        assertFalse(MillerRabin.isPrime(4L, 10));
        assertFalse(MillerRabin.isPrime(15L, 10));

        // BigInteger token generation
        BigInteger primeToken = MillerRabin.generateSecurePrime(32, 10);
        assertTrue(MillerRabin.testPrimality(primeToken, 10).isProbablePrime);
    }
}
