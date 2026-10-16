package com.smarthospital;

import com.smarthospital.m1_strings.*;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class Module1Test {

    @Test
    public void testKmpSearch() {
        String text = "PATIENT HAS PNEUMONIA AND ACUTE PNEUMONIA SYMPTOMS";
        List<Integer> matches = KmpSearch.search(text, "PNEUMONIA");
        assertEquals(2, matches.size());
        assertEquals(12, matches.get(0));
    }

    @Test
    public void testZFunction() {
        String text = "DIABETES_MELLITUS_PATIENT_WITH_DIABETES";
        List<Integer> matches = ZFunction.searchPattern(text, "DIABETES");
        assertEquals(2, matches.size());
    }

    @Test
    public void testRabinKarp() {
        String text = "ICD10-J18.9 DIAGNOSTIC REPORT FOR ICD10-J18.9";
        List<Integer> matches = RabinKarp.search(text, "ICD10-J18.9");
        assertEquals(2, matches.size());
    }

    @Test
    public void testAhoCorasick() {
        AhoCorasick ac = new AhoCorasick(Arrays.asList("ASTHMA", "BRONCHITIS", "COUGH"));
        String report = "PATIENT COMPLAINS OF ASTHMA AND SEVERE COUGH";
        List<AhoCorasick.MatchResult> results = ac.searchIn(report);
        assertEquals(2, results.size());
    }
}
