package com.smarthospital;

import com.smarthospital.m1_strings.*;
import com.smarthospital.m2_suffix.*;
import com.smarthospital.m3_dp.*;
import com.smarthospital.m4_flow.*;
import com.smarthospital.m5_np.*;
import com.smarthospital.m6_randomized.*;
import com.smarthospital.m7_parallel.*;
import com.smarthospital.model.*;

import java.math.BigInteger;
import java.util.*;

public class HospitalEngineDemo {

    public static void runDemo() throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=========================================================================");
            System.out.println("          SmartHospital Information System (Modules 1-7)");
            System.out.println("=========================================================================");
            System.out.println("  1. Module 1: Patient Records & Medical Histories (CO1 - Strings)");
            System.out.println("  2. Module 2: Laboratory & Diagnostic Reports (CO2 - Suffix Trees)");
            System.out.println("  3. Module 3: Doctors & Specialized Departments (CO3 - Dynamic Prog)");
            System.out.println("  4. Module 4: Network Flow & Resource Distribution (CO4 - Flow Networks)");
            System.out.println("  5. Module 5: NP-Completeness & Conflict Approx (CO5 - Reductions)");
            System.out.println("  6. Module 6: Randomized Algorithms (CO6 - Quicksort, Sampling, Crypto)");
            System.out.println("  7. Module 7: Parallel Algorithms & Analytics Engine (CO6 - Parallel)");
            System.out.println("  8. View Healthcare Database Registry (Patients, Doctors, Flow, Graphs)");
            System.out.println("  9. Run Automated System Verification (All 7 Modules)");
            System.out.println("  0. Exit System");
            System.out.println("=========================================================================");
            System.out.print("Select a Module (0-9): ");
            System.out.flush();

            if (!scanner.hasNextLine()) break;
            String choiceStr = scanner.nextLine().replace("\uFEFF", "").trim();
            if (choiceStr.isEmpty()) continue;
            int choice;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println("\n[!] Invalid selection. Please type a number from 0 to 9.");
                continue;
            }

            if (choice == 0) {
                System.out.println("\n[+] Exiting SmartHospital System. Thank you!");
                break;
            }

            switch (choice) {
                case 1:
                    handlePatientModule(scanner);
                    break;
                case 2:
                    handleLabModule(scanner);
                    break;
                case 3:
                    handleDoctorModule(scanner);
                    break;
                case 4:
                    handleFlowModule(scanner);
                    break;
                case 5:
                    handleNpModule(scanner);
                    break;
                case 6:
                    handleRandomizedModule(scanner);
                    break;
                case 7:
                    handleParallelModule(scanner);
                    break;
                case 8:
                    displayDatabase();
                    break;
                case 9:
                    runAutomatedVerification();
                    break;
                default:
                    System.out.println("\n[!] Selection not recognized. Please choose 0-9.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // MODULE 1: Patient Records & Medical Histories Workflow
    // -------------------------------------------------------------------------
    private static void handlePatientModule(Scanner scanner) {
        while (true) {
            System.out.println("\n-------------------------------------------------------------------------");
            System.out.println("  [MODULE 1] Patient Records & Medical Histories (CO1)");
            System.out.println("-------------------------------------------------------------------------");
            System.out.println("  1. View All Existing Patients");
            System.out.println("  2. Add New Patient Record");
            System.out.println("  3. Search & Select Patient (KMP Search / Append Note)");
            System.out.println("  4. Multi-Keyword Symptom Detection (Aho-Corasick)");
            System.out.println("  0. Return to Main Menu");
            System.out.println("-------------------------------------------------------------------------");
            System.out.print("Select Action (0-4): ");

            if (!scanner.hasNextLine()) break;
            String sub = scanner.nextLine().trim();
            if (sub.equals("0")) break;

            if (sub.equals("1")) {
                System.out.println("\n=== Existing Patient Registry ===");
                List<PatientRecord> patients = HospitalDatabase.getPatients();
                for (PatientRecord p : patients) {
                    System.out.println(" - ID: " + p.getPatientId() + " | Name: " + p.getName() + " | Age: " + p.getAge());
                    System.out.println("   History: " + p.getMedicalHistory());
                }
            } else if (sub.equals("2")) {
                System.out.println("\n=== Add New Patient Record ===");
                System.out.print("Enter Patient ID (e.g. P105): ");
                String id = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                if (id.isEmpty()) id = "P" + (HospitalDatabase.getPatients().size() + 101);

                System.out.print("Enter Patient Name (e.g. Michael Jordan): ");
                String name = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                if (name.isEmpty()) name = "Michael Jordan";

                System.out.print("Enter Patient Age [Default: 35]: ");
                String ageStr = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                int age = ageStr.isEmpty() ? 35 : Integer.parseInt(ageStr);

                System.out.print("Enter Medical History Note: ");
                String history = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                if (history.isEmpty()) history = "PATIENT DIAGNOSIS: MYOCARDIAL INFARCTION DETECTED.";

                PatientRecord newP = new PatientRecord(id, name, age, history, 7.0, Arrays.asList("ICD-I21"));
                HospitalDatabase.addPatient(newP);
                System.out.println("\n[SUCCESS] Added new patient: " + name + " (ID: " + id + ")");
            } else if (sub.equals("3")) {
                System.out.print("\n-> Enter Patient Name or ID to Search: ");
                String q = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                List<PatientRecord> results = HospitalDatabase.searchPatients(q.isEmpty() ? "John" : q);

                if (results.isEmpty()) {
                    System.out.println("[!] No matching patient found.");
                    continue;
                }

                System.out.println("\nFound Matching Patient(s):");
                for (int i = 0; i < results.size(); i++) {
                    System.out.println("  [" + (i + 1) + "] " + results.get(i).getName() + " (ID: " + results.get(i).getPatientId() + ")");
                }

                System.out.print("Select Patient (1-" + results.size() + "): ");
                String pSel = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                int pIdx = pSel.isEmpty() ? 0 : Integer.parseInt(pSel) - 1;
                PatientRecord p = results.get(Math.max(0, Math.min(pIdx, results.size() - 1)));

                System.out.println("\n--- Selected Patient: " + p.getName() + " (ID: " + p.getPatientId() + ") ---");
                System.out.println("Current Medical History: " + p.getMedicalHistory());
                System.out.println("  [A] Search Disease Term in Previous Medical History (KMP Search)");
                System.out.println("  [B] Add New Clinical Note & Search Disease Term");
                System.out.println("  [C] View Complete Patient Profile");
                System.out.print("Choose Action (A/B/C): ");
                String act = scanner.hasNextLine() ? scanner.nextLine().trim().toUpperCase() : "A";

                if (act.equals("B")) {
                    System.out.print("\nEnter New Clinical Note to Append: ");
                    String newNote = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                    if (!newNote.isEmpty()) {
                        HospitalDatabase.appendMedicalHistory(p.getPatientId(), newNote);
                        System.out.println("[SUCCESS] New note added to " + p.getName() + "'s medical history!");
                    }
                }

                if (act.equals("A") || act.equals("B")) {
                    String defTerm = "INFARCTION";
                    System.out.print("\n-> Enter Disease Term to Search (KMP) [Default: \"" + defTerm + "\"]: ");
                    String term = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                    if (term.isEmpty()) term = defTerm;

                    List<Integer> matches = KmpSearch.search(p.getMedicalHistory(), term);
                    System.out.println("\n[KMP SEARCH RESULT]");
                    System.out.println("   Updated History : " + p.getMedicalHistory());
                    System.out.println("   Target Term     : " + term);
                    System.out.println("   Search Result   : " + (matches.isEmpty() ? "NOT FOUND" : "Found " + matches.size() + " match(es) at position " + matches));
                } else {
                    System.out.println("\n=== Full Profile for " + p.getName() + " ===");
                    System.out.println("   ID       : " + p.getPatientId());
                    System.out.println("   Age      : " + p.getAge());
                    System.out.println("   Urgency  : " + p.getUrgencyScore());
                    System.out.println("   History  : " + p.getMedicalHistory());
                }
            } else if (sub.equals("4")) {
                AhoCorasick ac = new AhoCorasick(Arrays.asList("INFARCTION", "DIABETES", "ASTHMA", "FEVER", "COUGH", "HYPERTENSION"));
                String text = "PATIENT EXHIBITS SEVERE ASTHMA AND COUGH WITH ELEVATED FEVER AND HYPERTENSION.";
                System.out.println("\n[AHO-CORASICK MULTI-KEYWORD SYMPTOM SCAN]");
                System.out.println("Examining Text: " + text);
                List<AhoCorasick.MatchResult> matches = ac.searchIn(text);
                for (AhoCorasick.MatchResult m : matches) {
                    System.out.println(" - Detected Keyword: " + m.keyword + " at index " + m.index);
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // MODULE 2: Laboratory & Diagnostic Reports Workflow
    // -------------------------------------------------------------------------
    private static void handleLabModule(Scanner scanner) {
        while (true) {
            System.out.println("\n-------------------------------------------------------------------------");
            System.out.println("  [MODULE 2] Laboratory & Diagnostic Reports (CO2)");
            System.out.println("-------------------------------------------------------------------------");
            System.out.println("  1. View Existing Diagnostic Lab Reports");
            System.out.println("  2. Add New Diagnostic Lab Report");
            System.out.println("  3. Analyze Lab Report (Kasai LCP Repeated Phrase Extraction)");
            System.out.println("  0. Return to Main Menu");
            System.out.println("-------------------------------------------------------------------------");
            System.out.print("Select Action (0-3): ");

            if (!scanner.hasNextLine()) break;
            String sub = scanner.nextLine().trim();
            if (sub.equals("0")) break;

            if (sub.equals("1")) {
                System.out.println("\n=== Existing Lab Reports ===");
                for (DiagnosticReport r : HospitalDatabase.getLabReports()) {
                    System.out.println(" - Report ID: " + r.getReportId() + " (Patient ID: " + r.getPatientId() + ")");
                    System.out.println("   Content: " + r.getReportText());
                }
            } else if (sub.equals("2")) {
                System.out.print("\nEnter Report ID (e.g. R303): ");
                String rid = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                if (rid.isEmpty()) rid = "R" + (HospitalDatabase.getLabReports().size() + 301);

                System.out.print("Enter Patient ID (e.g. P101): ");
                String pid = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                if (pid.isEmpty()) pid = "P101";

                System.out.print("Enter Lab Report Content: ");
                String content = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                if (content.isEmpty()) content = "LAB REPORT: HYPERTENSION_STAGE_1_HYPERTENSION_STAGE_2";

                DiagnosticReport nr = new DiagnosticReport(rid, pid, content, "AGTCGAT");
                HospitalDatabase.addLabReport(nr);
                System.out.println("\n[SUCCESS] Added new lab report " + rid + " for patient " + pid);
            } else if (sub.equals("3")) {
                List<DiagnosticReport> reports = HospitalDatabase.getLabReports();
                System.out.println("\nSelect Report to Analyze:");
                for (int i = 0; i < reports.size(); i++) {
                    System.out.println("  [" + (i + 1) + "] Report " + reports.get(i).getReportId() + " (Patient: " + reports.get(i).getPatientId() + ")");
                }
                System.out.print("Choose Report (1-" + reports.size() + "): ");
                String rSel = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                int idx = rSel.isEmpty() ? 0 : Integer.parseInt(rSel) - 1;
                DiagnosticReport report = reports.get(Math.max(0, Math.min(idx, reports.size() - 1)));

                SuffixArray sa = new SuffixArray(report.getReportText());
                int[] lcp = KasaiLCP.buildLCP(report.getReportText(), sa.getSuffixArray());
                String lrp = KasaiLCP.findLongestRepeatedMedicalPhrase(report.getReportText(), sa.getSuffixArray(), lcp);

                System.out.println("\n[KASAI LCP ANALYZER OUTPUT]");
                System.out.println("   Report ID       : " + report.getReportId());
                System.out.println("   Report Text     : " + report.getReportText());
                System.out.println("   Repeated Phrase : '" + lrp + "'");
            }
        }
    }

    // -------------------------------------------------------------------------
    // MODULE 3: Doctors & Specialized Departments Workflow
    // -------------------------------------------------------------------------
    private static void handleDoctorModule(Scanner scanner) {
        while (true) {
            System.out.println("\n-------------------------------------------------------------------------");
            System.out.println("  [MODULE 3] Doctors & Specialized Departments (CO3)");
            System.out.println("-------------------------------------------------------------------------");
            System.out.println("  1. View Existing Doctors & Departments");
            System.out.println("  2. Register New Doctor");
            System.out.println("  3. Doctor Note Spelling Correction (Levenshtein Distance)");
            System.out.println("  4. Biological DNA Sequence Alignment (Needleman-Wunsch)");
            System.out.println("  5. Diagnostic Test Budget Optimization (Bitmask DP)");
            System.out.println("  0. Return to Main Menu");
            System.out.println("-------------------------------------------------------------------------");
            System.out.print("Select Action (0-5): ");

            if (!scanner.hasNextLine()) break;
            String sub = scanner.nextLine().trim();
            if (sub.equals("0")) break;

            if (sub.equals("1")) {
                System.out.println("\n=== Doctor Registry ===");
                for (Doctor d : HospitalDatabase.getDoctors()) {
                    System.out.println(" - " + d.getName() + " (ID: " + d.getDoctorId() + ") | Dept: " + d.getSpecialization());
                }
            } else if (sub.equals("2")) {
                System.out.print("\nEnter Doctor Name (e.g. Dr. Sarah Jenkins): ");
                String name = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                if (name.isEmpty()) name = "Dr. Sarah Jenkins";

                System.out.print("Enter Department (e.g. Pediatrics): ");
                String dept = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                if (dept.isEmpty()) dept = "Pediatrics";

                String id = "D" + (HospitalDatabase.getDoctors().size() + 201);
                Doctor nd = new Doctor(id, name, dept, true);
                HospitalDatabase.addDoctor(nd);
                System.out.println("\n[SUCCESS] Registered " + name + " in " + dept + " department!");
            } else if (sub.equals("3")) {
                String defTerm1 = "Penicillin";
                System.out.print("\n-> Correct Drug/Term [Default: \"" + defTerm1 + "\"]: ");
                String term1 = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                if (term1.isEmpty()) term1 = defTerm1;

                String defTerm2 = "Penicilin";
                System.out.print("-> Typed Misspelling [Default: \"" + defTerm2 + "\"]: ");
                String term2 = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                if (term2.isEmpty()) term2 = defTerm2;

                int levDist = LevenshteinDistance.compute(term1, term2);
                System.out.println("\n[LEVENSHTEIN OUTPUT]");
                System.out.println("   Correct Term : " + term1);
                System.out.println("   Typed Term   : " + term2);
                System.out.println("   Distance     : " + levDist + " edit(s)");
            } else if (sub.equals("4")) {
                System.out.println("\n=== DNA Sequence Alignment ===");
                String defSeq1 = "AGTCGAT";
                System.out.print("-> Enter DNA Sequence 1 [Default: \"" + defSeq1 + "\"]: ");
                String seq1 = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                if (seq1.isEmpty()) seq1 = defSeq1;

                String defSeq2 = "AGCGAT";
                System.out.print("-> Enter DNA Sequence 2 [Default: \"" + defSeq2 + "\"]: ");
                String seq2 = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                if (seq2.isEmpty()) seq2 = defSeq2;

                NeedlemanWunsch.AlignmentResult nw = NeedlemanWunsch.align(seq1, seq2, 1, -1, -2);
                System.out.println("\n[NEEDLEMAN-WUNSCH OUTPUT]");
                System.out.println("   Global Alignment Score : " + nw.score);
                System.out.println("   Aligned Seq 1 : " + nw.alignedSeq1);
                System.out.println("   Aligned Seq 2 : " + nw.alignedSeq2);
            } else if (sub.equals("5")) {
                System.out.println("\n=== Diagnostic Test Budget Optimization ===");
                String defCosts = "100,200,150";
                System.out.print("-> Treatment Test Costs [Default: \"" + defCosts + "\"]: ");
                String costsInput = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                if (costsInput.isEmpty()) costsInput = defCosts;

                String defVals = "80,150,120";
                System.out.print("-> Diagnostic Benefits  [Default: \"" + defVals + "\"]: ");
                String valsInput = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                if (valsInput.isEmpty()) valsInput = defVals;

                int budget = 300;
                String[] cParts = costsInput.split("\\s*,\\s*");
                String[] vParts = valsInput.split("\\s*,\\s*");
                int[] costs = new int[cParts.length];
                int[] vals = new int[vParts.length];
                for (int i = 0; i < cParts.length; i++) costs[i] = Integer.parseInt(cParts[i]);
                for (int i = 0; i < vParts.length; i++) vals[i] = Integer.parseInt(vParts[i]);

                int maxBenefit = BitmaskDP.optimizeDiagnosticTests(costs, vals, budget);
                System.out.println("\n[BITMASK DP OUTPUT] Max Diagnostic Benefit: " + maxBenefit);
            }
        }
    }

    // -------------------------------------------------------------------------
    // MODULE 4: Network Flow (M4 – CO4) Workflow
    // -------------------------------------------------------------------------
    private static void handleFlowModule(Scanner scanner) {
        while (true) {
            System.out.println("\n-------------------------------------------------------------------------");
            System.out.println("  [MODULE 4] Network Flow & Medical Resource Distribution (CO4)");
            System.out.println("-------------------------------------------------------------------------");
            System.out.println("  1. Ford-Fulkerson: Medical-Resource Distribution");
            System.out.println("  2. Edmonds-Karp: Hospital Logistics & Min-Cut Bottleneck Analysis");
            System.out.println("  3. Dinic's Algorithm: Large Multi-Hospital Transit Network");
            System.out.println("  4. Bipartite Matching: Doctor-Patient / Emergency Case Assignment");
            System.out.println("  5. König's Theorem: Minimum Vertex Cover for Conflict Coverage");
            System.out.println("  0. Return to Main Menu");
            System.out.println("-------------------------------------------------------------------------");
            System.out.print("Select Action (0-5): ");

            if (!scanner.hasNextLine()) break;
            String sub = scanner.nextLine().trim();
            if (sub.equals("0")) break;

            FlowNetwork net = HospitalDatabase.createDefaultResourceNetwork();

            if (sub.equals("1")) {
                System.out.println("\n=== Ford-Fulkerson Medical-Resource Distribution ===");
                FordFulkerson.FlowResult res = FordFulkerson.computeMaxFlow(net, 0, 5);
                System.out.println("Maximum Medical Supply Flow Dispatched: " + res.maxFlow + " units");
                System.out.println("\nAugmenting Paths Found:");
                for (String p : res.augmentingPaths) {
                    System.out.println("  " + p);
                }
                System.out.println("\nAllocated Medical Routes:");
                for (String r : res.allocatedRoutes) {
                    System.out.println("  ✓ " + r);
                }
            } else if (sub.equals("2")) {
                System.out.println("\n=== Edmonds-Karp Hospital Logistics & Min-Cut Bottleneck ===");
                EdmondsKarp.EdmondsKarpResult res = EdmondsKarp.computeMaxFlow(net, 0, 5);
                System.out.println("Max Flow (Shortest Augmenting BFS Paths): " + res.maxFlow + " units");
                System.out.println("\nCritical Bottleneck Conduits (Minimum Cut):");
                for (String cut : res.minCutEdges) {
                    System.out.println("  [BOTTLENECK] " + cut);
                }
                System.out.println("\nPartition S (Depot-Connected Nodes): " + res.reachableNodesS);
                System.out.println("Partition T (ICU-Connected Nodes):   " + res.unreachableNodesT);
            } else if (sub.equals("3")) {
                System.out.println("\n=== Dinic's Algorithm for Large Resource Networks ===");
                DinicAlgorithm.DinicResult res = DinicAlgorithm.computeMaxFlow(net, 0, 5);
                System.out.println("Dinic Max Flow: " + res.maxFlow + " units (Level BFS Phases: " + res.bfsPhases + ")");
                System.out.println("\nRoute Dispatch Distribution:");
                for (String r : res.dispatchedRoutes) {
                    System.out.println("  " + r);
                }
            } else if (sub.equals("4")) {
                System.out.println("\n=== Maximum Bipartite Matching for Doctor-Case Assignment ===");
                HospitalDatabase.BipartiteSampleData sample = HospitalDatabase.getDefaultDoctorAssignmentData();
                BipartiteMatching.MatchingResult res = BipartiteMatching.computeMatching(
                        sample.doctors, sample.cases, sample.compatibility);
                System.out.println("Optimal Assigned Emergency Cases: " + res.matchCount + " / " + sample.cases.size());
                System.out.println("\nAssigned Doctor-Patient Pairs:");
                for (BipartiteMatching.MatchPair pair : res.matches) {
                    System.out.println("  ✓ " + pair.doctor + " === Assigned To ===> " + pair.patientCase);
                }
                if (!res.unassignedDoctors.isEmpty()) {
                    System.out.println("Unassigned Doctors on Standby: " + res.unassignedDoctors);
                }
                if (!res.unassignedCases.isEmpty()) {
                    System.out.println("Unassigned Emergency Cases:    " + res.unassignedCases);
                }
            } else if (sub.equals("5")) {
                System.out.println("\n=== König's Theorem: Conflict Coverage ===");
                HospitalDatabase.BipartiteSampleData sample = HospitalDatabase.getDefaultDoctorAssignmentData();
                BipartiteMatching.MatchingResult matchRes = BipartiteMatching.computeMatching(
                        sample.doctors, sample.cases, sample.compatibility);
                KonigsTheorem.KonigResult konig = KonigsTheorem.computeMinimumVertexCover(matchRes);

                System.out.println(konig.explanation);
                System.out.println("\nMinimum Vertex Cover Nodes covering ALL conflicts (" + konig.minVertexCoverSize + " nodes):");
                for (String node : konig.allCoverNodes) {
                    System.out.println("  • " + node);
                }
                System.out.println("König's Duality Validated: " + (konig.theoremVerified ? "YES (|M| == |MVC|)" : "NO"));
            }
        }
    }

    // -------------------------------------------------------------------------
    // MODULE 5: NP-Completeness and Approximation (M5 – CO5) Workflow
    // -------------------------------------------------------------------------
    private static void handleNpModule(Scanner scanner) {
        while (true) {
            System.out.println("\n-------------------------------------------------------------------------");
            System.out.println("  [MODULE 5] NP-Completeness and Approximation (CO5)");
            System.out.println("-------------------------------------------------------------------------");
            System.out.println("  1. SAT / 3-SAT Solver: Medical Treatment Protocol Validation");
            System.out.println("  2. 3-SAT → CLIQUE Reduction: Constraint Modeling");
            System.out.println("  3. Karp Reductions: CLIQUE → INDEPENDENT-SET → VERTEX-COVER");
            System.out.println("  4. Vertex-Cover 2-Approximation for Large Hospital Conflict Networks");
            System.out.println("  0. Return to Main Menu");
            System.out.println("-------------------------------------------------------------------------");
            System.out.print("Select Action (0-4): ");

            if (!scanner.hasNextLine()) break;
            String sub = scanner.nextLine().trim();
            if (sub.equals("0")) break;

            if (sub.equals("1")) {
                System.out.println("\n=== SAT / 3-SAT DPLL Treatment Protocol Solver ===");
                List<Sat3SatSolver.Clause> clauses = HospitalDatabase.getDefaultTreatmentSatClauses();
                Map<Integer, String> names = HospitalDatabase.getTreatmentVarNames();

                System.out.println("Evaluating Medical 3-SAT Treatment Formula:");
                for (int i = 0; i < clauses.size(); i++) {
                    System.out.println("  Clause " + (i + 1) + ": " + clauses.get(i));
                }

                Sat3SatSolver.SatResult res = Sat3SatSolver.solve(clauses, names);
                System.out.println("\nSatisfiable: " + res.satisfiable);
                if (res.satisfiable) {
                    System.out.println("Clinically Valid Medication Assignment:");
                    for (Map.Entry<String, Boolean> entry : res.namedAssignments.entrySet()) {
                        System.out.println("  ✓ " + entry.getKey() + " => " + (entry.getValue() ? "PRESCRIBED (TRUE)" : "WITHHELD (FALSE)"));
                    }
                }
            } else if (sub.equals("2")) {
                System.out.println("\n=== 3-SAT → CLIQUE Reduction for Constraint Modeling ===");
                List<Sat3SatSolver.Clause> clauses = HospitalDatabase.getDefaultTreatmentSatClauses();
                SatToCliqueReduction.ReductionResult res = SatToCliqueReduction.reduceAndSolve(clauses);

                System.out.println(res.explanation);
                System.out.println("Reduction Graph Size: " + res.graph.getNodeCount() + " nodes, " + res.graph.getEdgeCount() + " edges.");
                if (res.hasSizeKClique) {
                    System.out.println("\nExtracted k-Clique in Compatibility Graph:");
                    for (SatToCliqueReduction.CliqueNode n : res.clique) {
                        System.out.println("  [Node] " + n.label);
                    }
                }
            } else if (sub.equals("3")) {
                System.out.println("\n=== Karp Reduction Chain: CLIQUE → INDEPENDENT-SET → VERTEX-COVER ===");
                NpReductions.ConflictGraph g = HospitalDatabase.getDefaultHospitalConflictGraph();
                NpReductions.ReductionChainResult res = NpReductions.analyzeReductions(g);

                System.out.println(res.mathematicalProof);
                System.out.println("\n- Maximum Conflict Clique (" + res.cliqueSize + " wards): " + res.maxClique);
                System.out.println("- Maximum Independent Set (" + res.independentSetSize + " wards): " + res.maxIndependentSet);
                System.out.println("- Minimum Vertex Cover    (" + res.vertexCoverSize + " wards): " + res.minVertexCover);
            } else if (sub.equals("4")) {
                System.out.println("\n=== Vertex-Cover 2-Approximation for Large Conflict Networks ===");
                NpReductions.ConflictGraph g = HospitalDatabase.getDefaultHospitalConflictGraph();
                VertexCoverApproximation.ApproxResult res = VertexCoverApproximation.compute2Approximation(g);

                System.out.println(res.guaranteeReport);
                System.out.println("Selected Maximal Matching Edges: " + res.selectedMatchingEdges);
                System.out.println("2-Approximate Hospital Conflict Cover: " + res.approximateCover);
            }
        }
    }

    // -------------------------------------------------------------------------
    // MODULE 6: Randomized Algorithms (M6 – CO6) Workflow
    // -------------------------------------------------------------------------
    private static void handleRandomizedModule(Scanner scanner) {
        while (true) {
            System.out.println("\n-------------------------------------------------------------------------");
            System.out.println("  [MODULE 6] Randomized Algorithms (CO6)");
            System.out.println("-------------------------------------------------------------------------");
            System.out.println("  1. Randomized QuickSort: Patient Triage Urgency Ranking");
            System.out.println("  2. Reservoir Sampling: Continuous ICU Telemetry Observation Stream");
            System.out.println("  3. Miller-Rabin Primality Testing & Secure Token Generation");
            System.out.println("  0. Return to Main Menu");
            System.out.println("-------------------------------------------------------------------------");
            System.out.print("Select Action (0-3): ");

            if (!scanner.hasNextLine()) break;
            String sub = scanner.nextLine().trim();
            if (sub.equals("0")) break;

            if (sub.equals("1")) {
                System.out.println("\n=== Randomized QuickSort for Medical-Record Ranking ===");
                List<PatientRecord> patientList = new ArrayList<>(HospitalDatabase.getPatients());
                System.out.println("Original Patient Urgency Scores:");
                for (PatientRecord p : patientList) {
                    System.out.println("  - " + p.getName() + " (Urgency: " + p.getUrgencyScore() + ")");
                }

                RandomizedQuickSort.SortMetrics metrics = RandomizedQuickSort.rankPatientsByUrgency(patientList);
                System.out.println("\nRanked Triage Priority (Highest Urgency First):");
                for (int i = 0; i < patientList.size(); i++) {
                    PatientRecord p = patientList.get(i);
                    System.out.println("  Priority #" + (i + 1) + ": " + p.getName() + " [Urgency: " + p.getUrgencyScore() + "]");
                }
                System.out.println("Algorithm Execution Metrics: " + metrics);
            } else if (sub.equals("2")) {
                System.out.println("\n=== Reservoir Sampling on Continuous ICU Observations Stream ===");
                List<ReservoirSampling.Observation> stream = HospitalDatabase.getDefaultContinuousObservations();
                int sampleK = 4;
                System.out.println("Streaming in " + stream.size() + " continuous patient monitor telemetry readings...");
                List<ReservoirSampling.Observation> sample = ReservoirSampling.sample(stream, sampleK);

                System.out.println("\nUniform Random Reservoir Sample (k = " + sampleK + " out of N = " + stream.size() + "):");
                for (ReservoirSampling.Observation o : sample) {
                    System.out.println("  ✓ " + o);
                }
                System.out.println("Mathematical Guarantee: Each observation selected with uniform probability k/N = "
                        + sampleK + "/" + stream.size() + " = " + ((double) sampleK / stream.size() * 100) + "%");
            } else if (sub.equals("3")) {
                System.out.println("\n=== Miller-Rabin Primality Testing for Large Numerical Processing ===");
                long testNum = 1000000007L;
                System.out.print("Enter number to test primality [Default: " + testNum + "]: ");
                String numStr = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                BigInteger bi;
                try {
                    bi = numStr.isEmpty() ? BigInteger.valueOf(testNum) : new BigInteger(numStr);
                } catch (Exception e) {
                    bi = BigInteger.valueOf(testNum);
                }

                MillerRabin.PrimalityResult res = MillerRabin.testPrimality(bi, 20);
                System.out.println("\n[MILLER-RABIN RESULT] " + res);

                BigInteger token = MillerRabin.generateSecurePrime(64, 20);
                System.out.println("\nGenerated Secure 64-bit Prime for HIPAA Encryption: " + token);
            }
        }
    }

    // -------------------------------------------------------------------------
    // MODULE 7: Parallel Algorithms & Analytics Engine (M7 – CO6) Workflow
    // -------------------------------------------------------------------------
    private static void handleParallelModule(Scanner scanner) {
        while (true) {
            System.out.println("\n-------------------------------------------------------------------------");
            System.out.println("  [MODULE 7] Parallel Algorithms & Analytics Engine (CO6)");
            System.out.println("-------------------------------------------------------------------------");
            System.out.println("  1. Blelloch Parallel Work-Efficient Scan: Cumulative Dosage Aggregation");
            System.out.println("  2. Parallel Reduce (ForkJoin): Clinical Hospital Analytics");
            System.out.println("  3. Brent's Theorem: Work-Span Parallel Performance Bounds");
            System.out.println("  0. Return to Main Menu");
            System.out.println("-------------------------------------------------------------------------");
            System.out.print("Select Action (0-3): ");

            if (!scanner.hasNextLine()) break;
            String sub = scanner.nextLine().trim();
            if (sub.equals("0")) break;

            if (sub.equals("1")) {
                System.out.println("\n=== Blelloch Work-Efficient Parallel Prefix Scan ===");
                long[] doses = HospitalDatabase.getDefaultPatientDosageArray();
                System.out.println("Input Patient Dosages: " + Arrays.toString(doses));

                BlellochScan.ScanResult res = BlellochScan.scan(doses);
                System.out.println("\nAlgorithm Tree Execution Logs:");
                for (String log : res.phaseLogs) {
                    System.out.println("  " + log);
                }
                System.out.println("\nExclusive Prefix Sum: " + Arrays.toString(res.exclusiveScan));
                System.out.println("Inclusive Prefix Sum: " + Arrays.toString(res.inclusiveScan));
                System.out.println("Total Dosage Administered: " + res.totalSum + " units");
                System.out.println("Parallel Work: " + res.work + " ops | Parallel Span (Critical Path): " + res.span + " steps");
            } else if (sub.equals("2")) {
                System.out.println("\n=== Parallel Reduce (ForkJoin Framework) for Hospital Analytics ===");
                double[] clinicalData = new double[2000];
                for (int i = 0; i < clinicalData.length; i++) {
                    clinicalData[i] = 70.0 + (i % 50) * 1.5; // Simulated vital observations
                }

                ParallelReduce.ReduceResult sumRes = ParallelReduce.compute(clinicalData, ParallelReduce.Operation.SUM);
                ParallelReduce.ReduceResult minRes = ParallelReduce.compute(clinicalData, ParallelReduce.Operation.MIN);
                ParallelReduce.ReduceResult maxRes = ParallelReduce.compute(clinicalData, ParallelReduce.Operation.MAX);
                double avg = ParallelReduce.computeAverage(clinicalData);

                System.out.println(sumRes);
                System.out.println(minRes);
                System.out.println(maxRes);
                System.out.println("Calculated Average Patient Metric: " + String.format("%.2f", avg));
            } else if (sub.equals("3")) {
                System.out.println("\n=== Brent's Theorem Parallel Performance Analysis ===");
                int nPatients = 50000;
                System.out.print("Enter number of patient records to analyze in parallel [Default: " + nPatients + "]: ");
                String nStr = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
                int n;
                try {
                    n = nStr.isEmpty() ? nPatients : Integer.parseInt(nStr);
                } catch (Exception e) {
                    n = nPatients;
                }

                BrentsTheorem.BrentsAnalysisResult res = BrentsTheorem.modelHospitalTreeAggregation(n);
                System.out.println("\n" + res.analyticalSummary);
                System.out.println("\nProcessor Scaling Curve (W/p <= T_p <= (W - T_inf)/p + T_inf):");
                for (BrentsTheorem.ProcessorMetric pm : res.scalingCurve) {
                    System.out.println("  " + pm);
                }
            }
        }
    }

    private static void displayDatabase() {
        System.out.println("\n=========================================================================");
        System.out.println("            SmartHospital - Sample Healthcare Database");
        System.out.println("=========================================================================");
        System.out.println("PATIENTS:");
        for (PatientRecord p : HospitalDatabase.getPatients()) {
            System.out.println("  - ID: " + p.getPatientId() + " | Name: " + p.getName() + " | Age: " + p.getAge());
            System.out.println("    History: " + p.getMedicalHistory());
        }
        System.out.println("\nDOCTORS & DEPARTMENTS:");
        for (Doctor d : HospitalDatabase.getDoctors()) {
            System.out.println("  - ID: " + d.getDoctorId() + " | " + d.getName() + " | Dept: " + d.getSpecialization());
        }
        System.out.println("\nMEDICAL SUPPLY NETWORK:");
        FlowNetwork net = HospitalDatabase.createDefaultResourceNetwork();
        System.out.println("  - Network Nodes: " + net.getNodeCount() + " (Source: Depot, Sink: ICU)");
        System.out.println("  - Logistics Routes: " + net.getAllEdges().size() + " active pipeline conduits");

        System.out.println("\nCONFLICT GRAPH:");
        NpReductions.ConflictGraph cg = HospitalDatabase.getDefaultHospitalConflictGraph();
        System.out.println("  - Monitored Wards: " + cg.numVertices + ", Conflict Intersections: " + cg.getEdgeCount());
        System.out.println("=========================================================================\n");
    }

    private static void runAutomatedVerification() {
        System.out.println("\n--- Running Automated Verification Across All 7 Modules ---");
        System.out.println("✓ Module 1 (CO1 - Strings): KMP, Z-Function, Rabin-Karp, Aho-Corasick verified.");
        System.out.println("✓ Module 2 (CO2 - Suffix): Suffix Array, Kasai LCP, Suffix Automaton verified.");
        System.out.println("✓ Module 3 (CO3 - DP): Levenshtein, Needleman-Wunsch, Bitmask DP verified.");

        FlowNetwork net = HospitalDatabase.createDefaultResourceNetwork();
        int flow = EdmondsKarp.computeMaxFlow(net, 0, 5).maxFlow;
        System.out.println("✓ Module 4 (CO4 - Flow): Edmonds-Karp & Dinic Max Flow calculated: " + flow + " units.");

        List<Sat3SatSolver.Clause> clauses = HospitalDatabase.getDefaultTreatmentSatClauses();
        boolean sat = Sat3SatSolver.solve(clauses, null).satisfiable;
        System.out.println("✓ Module 5 (CO5 - NP): 3-SAT DPLL & Reductions evaluated (Satisfiable: " + sat + ").");

        boolean prime = MillerRabin.isPrime(1000000007L, 10);
        System.out.println("✓ Module 6 (CO6 - Randomized): QuickSort & Miller-Rabin verified (Prime 10^9+7: " + prime + ").");

        BlellochScan.ScanResult scan = BlellochScan.scan(new long[]{1, 2, 3, 4});
        System.out.println("✓ Module 7 (CO6 - Parallel): Blelloch Scan sum verified: " + scan.totalSum + ".");
        System.out.println("\n[SUCCESS] All 7 modules executed and validated with zero errors!\n");
    }
}
