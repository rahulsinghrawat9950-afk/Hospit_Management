package com.smarthospital;

import com.smarthospital.m2_suffix.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Module2Test {

    @Test
    public void testSuffixArrayAndKasai() {
        String text = "HYPERTENSION_STAGE_1_HYPERTENSION_STAGE_2";
        SuffixArray sa = new SuffixArray(text);
        assertTrue(sa.contains("HYPERTENSION"));
        assertTrue(sa.contains("STAGE_1"));
        assertFalse(sa.contains("CANCER"));

        int[] lcp = KasaiLCP.buildLCP(text, sa.getSuffixArray());
        String phrase = KasaiLCP.findLongestRepeatedMedicalPhrase(text, sa.getSuffixArray(), lcp);
        assertEquals("HYPERTENSION_STAGE_", phrase);
    }

    @Test
    public void testSuffixAutomaton() {
        String text = "CARDIOLOGY_NEUROLOGY_CARDIOLOGY";
        SuffixAutomaton sam = new SuffixAutomaton(text);
        assertTrue(sam.containsSubstring("CARDIOLOGY"));
        assertTrue(sam.containsSubstring("NEUROLOGY"));
        assertFalse(sam.containsSubstring("ONCOLOGY"));
    }
}
