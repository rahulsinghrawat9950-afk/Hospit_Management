package com.smarthospital.m1_strings;

import java.util.*;

/**
 * Module 1 (CO1): Aho-Corasick Algorithm
 * Used for multiple medical-keyword detection (e.g. searching 100s of disease/drug names in one pass).
 */
public class AhoCorasick {

    public static class MatchResult {
        public final int index;
        public final String keyword;

        public MatchResult(int index, String keyword) {
            this.index = index;
            this.keyword = keyword;
        }

        @Override
        public String toString() {
            return "Match{" + "at=" + index + ", term='" + keyword + '\'' + '}';
        }
    }

    private static class Node {
        Map<Character, Node> children = new HashMap<>();
        Node fail;
        List<String> output = new ArrayList<>();
    }

    private final Node root = new Node();

    public AhoCorasick(List<String> keywords) {
        for (String kw : keywords) {
            insert(kw);
        }
        buildFailureLinks();
    }

    private void insert(String kw) {
        Node curr = root;
        for (char c : kw.toCharArray()) {
            curr = curr.children.computeIfAbsent(c, k -> new Node());
        }
        curr.output.add(kw);
    }

    private void buildFailureLinks() {
        Queue<Node> queue = new LinkedList<>();
        for (Node child : root.children.values()) {
            child.fail = root;
            queue.add(child);
        }

        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            for (Map.Entry<Character, Node> entry : curr.children.entrySet()) {
                char c = entry.getKey();
                Node child = entry.getValue();

                Node f = curr.fail;
                while (f != null && !f.children.containsKey(c)) {
                    f = f.fail;
                }
                child.fail = (f == null) ? root : f.children.get(c);
                if (child.fail != null) {
                    child.output.addAll(child.fail.output);
                }
                queue.add(child);
            }
        }
    }

    public List<MatchResult> searchIn(String text) {
        List<MatchResult> results = new ArrayList<>();
        Node curr = root;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            while (curr != null && !curr.children.containsKey(c)) {
                curr = curr.fail;
            }
            if (curr == null) {
                curr = root;
                continue;
            }
            curr = curr.children.get(c);
            for (String kw : curr.output) {
                results.add(new MatchResult(i - kw.length() + 1, kw));
            }
        }
        return results;
    }
}
