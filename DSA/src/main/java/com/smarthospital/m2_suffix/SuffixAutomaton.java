package com.smarthospital.m2_suffix;

import java.util.HashMap;
import java.util.Map;

/**
 * Module 2 (CO2): Suffix Automaton
 * Linear-time deterministic finite automaton representing all substrings of medical reports.
 */
public class SuffixAutomaton {

    public static class State {
        public int len, link;
        public Map<Character, Integer> next = new HashMap<>();
    }

    private State[] st;
    private int sz, last;

    public SuffixAutomaton(String s) {
        int n = s.length();
        st = new State[2 * n];
        for (int i = 0; i < 2 * n; i++) st[i] = new State();
        st[0].len = 0;
        st[0].link = -1;
        sz = 1;
        last = 0;

        for (char c : s.toCharArray()) {
            extend(c);
        }
    }

    private void extend(char c) {
        int cur = sz++;
        st[cur].len = st[last].len + 1;
        int p = last;
        while (p != -1 && !st[p].next.containsKey(c)) {
            st[p].next.put(c, cur);
            p = st[p].link;
        }
        if (p == -1) {
            st[cur].link = 0;
        } else {
            int q = st[p].next.get(c);
            if (st[p].len + 1 == st[q].len) {
                st[cur].link = q;
            } else {
                int clone = sz++;
                st[clone].len = st[p].len + 1;
                st[clone].next = new HashMap<>(st[q].next);
                st[clone].link = st[q].link;
                while (p != -1 && st[p].next.get(c) == q) {
                    st[p].next.put(c, clone);
                    p = st[p].link;
                }
                st[q].link = clone;
                st[cur].link = clone;
            }
        }
        last = cur;
    }

    public boolean containsSubstring(String pattern) {
        int curr = 0;
        for (char c : pattern.toCharArray()) {
            if (!st[curr].next.containsKey(c)) return false;
            curr = st[curr].next.get(c);
        }
        return true;
    }
}
