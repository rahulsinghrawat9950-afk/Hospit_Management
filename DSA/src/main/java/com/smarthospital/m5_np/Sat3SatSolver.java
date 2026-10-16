package com.smarthospital.m5_np;

import java.util.*;

/**
 * Module 5 (CO5): SAT / 3-SAT Solver for Hospital Treatment and Resource Constraints.
 * Evaluates Boolean Satisfiability of complex medical treatment protocols and drug compatibility
 * using the DPLL (Davis-Putnam-Logemann-Loveland) complete decision algorithm.
 */
public class Sat3SatSolver {

    public static class Clause {
        public final List<Integer> literals;

        public Clause(int... lits) {
            this.literals = new ArrayList<>();
            for (int l : lits) {
                this.literals.add(l);
            }
        }

        public Clause(List<Integer> lits) {
            this.literals = new ArrayList<>(lits);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < literals.size(); i++) {
                int l = literals.get(i);
                sb.append(l > 0 ? "x" + l : "¬x" + (-l));
                if (i < literals.size() - 1) sb.append(" ∨ ");
            }
            sb.append(")");
            return sb.toString();
        }
    }

    public static class SatResult {
        public final boolean satisfiable;
        public final Map<Integer, Boolean> model;
        public final Map<String, Boolean> namedAssignments;
        public final String formulaString;

        public SatResult(boolean satisfiable, Map<Integer, Boolean> model,
                         Map<String, Boolean> namedAssignments, String formulaString) {
            this.satisfiable = satisfiable;
            this.model = model;
            this.namedAssignments = namedAssignments;
            this.formulaString = formulaString;
        }
    }

    public static boolean is3Sat(List<Clause> clauses) {
        for (Clause c : clauses) {
            if (c.literals.size() > 3) return false;
        }
        return true;
    }

    public static SatResult solve(List<Clause> formula, Map<Integer, String> varNames) {
        Set<Integer> variables = new TreeSet<>();
        for (Clause c : formula) {
            for (int lit : c.literals) {
                variables.add(Math.abs(lit));
            }
        }

        Map<Integer, Boolean> assignment = new HashMap<>();
        boolean sat = dpll(formula, assignment, variables);

        Map<String, Boolean> named = new LinkedHashMap<>();
        if (sat) {
            for (int v : variables) {
                boolean val = assignment.getOrDefault(v, true);
                String name = (varNames != null && varNames.containsKey(v)) ? varNames.get(v) : "x" + v;
                named.put(name, val);
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < formula.size(); i++) {
            sb.append(formula.get(i));
            if (i < formula.size() - 1) sb.append(" ∧ ");
        }

        return new SatResult(sat, sat ? assignment : Collections.emptyMap(), named, sb.toString());
    }

    private static boolean dpll(List<Clause> clauses, Map<Integer, Boolean> assignment, Set<Integer> allVars) {
        // Step 1: Simplify clauses under current assignment
        List<Clause> simplified = simplify(clauses, assignment);
        if (simplified == null) {
            // Found an empty clause -> contradiction
            return false;
        }
        if (simplified.isEmpty()) {
            // All clauses satisfied
            return true;
        }

        // Step 2: Unit Propagation
        for (Clause c : simplified) {
            if (c.literals.size() == 1) {
                int unitLit = c.literals.get(0);
                int var = Math.abs(unitLit);
                boolean val = unitLit > 0;
                assignment.put(var, val);
                boolean res = dpll(simplified, assignment, allVars);
                if (res) return true;
                assignment.remove(var);
                return false;
            }
        }

        // Step 3: Pure Literal Elimination
        Map<Integer, Integer> polarity = new HashMap<>(); // var -> +1, -1, or 0 (both)
        for (Clause c : simplified) {
            for (int lit : c.literals) {
                int v = Math.abs(lit);
                int sign = lit > 0 ? 1 : -1;
                if (!polarity.containsKey(v)) {
                    polarity.put(v, sign);
                } else if (polarity.get(v) != sign) {
                    polarity.put(v, 0);
                }
            }
        }
        for (Map.Entry<Integer, Integer> entry : polarity.entrySet()) {
            if (entry.getValue() != 0) {
                int v = entry.getKey();
                assignment.put(v, entry.getValue() > 0);
                boolean res = dpll(simplified, assignment, allVars);
                if (res) return true;
                assignment.remove(v);
                return false;
            }
        }

        // Step 4: Pick an unassigned variable and branch
        int branchVar = -1;
        for (int v : allVars) {
            if (!assignment.containsKey(v)) {
                branchVar = v;
                break;
            }
        }

        if (branchVar == -1) {
            return simplified.isEmpty();
        }

        // Try true branch
        assignment.put(branchVar, true);
        if (dpll(simplified, assignment, allVars)) {
            return true;
        }

        // Try false branch
        assignment.put(branchVar, false);
        if (dpll(simplified, assignment, allVars)) {
            return true;
        }

        assignment.remove(branchVar);
        return false;
    }

    private static List<Clause> simplify(List<Clause> clauses, Map<Integer, Boolean> assignment) {
        List<Clause> next = new ArrayList<>();
        for (Clause c : clauses) {
            boolean satisfied = false;
            List<Integer> remaining = new ArrayList<>();
            for (int lit : c.literals) {
                int var = Math.abs(lit);
                if (assignment.containsKey(var)) {
                    boolean val = assignment.get(var);
                    if ((lit > 0 && val) || (lit < 0 && !val)) {
                        satisfied = true;
                        break;
                    }
                } else {
                    remaining.add(lit);
                }
            }
            if (satisfied) {
                continue; // Clause is satisfied, omit from next round
            }
            if (remaining.isEmpty()) {
                // Clause cannot be satisfied under current assignment
                return null;
            }
            next.add(new Clause(remaining));
        }
        return next;
    }
}
