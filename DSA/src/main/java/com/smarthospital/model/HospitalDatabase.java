package com.smarthospital.model;

import com.smarthospital.m4_flow.FlowNetwork;
import com.smarthospital.m5_np.NpReductions;
import com.smarthospital.m5_np.Sat3SatSolver;
import com.smarthospital.m6_randomized.ReservoirSampling;

import java.util.*;
import java.util.stream.Collectors;

public class HospitalDatabase {

    private static final List<PatientRecord> PATIENTS = new ArrayList<>();
    private static final List<Doctor> DOCTORS = new ArrayList<>();
    private static final List<DiagnosticReport> LAB_REPORTS = new ArrayList<>();
    private static final List<String> MEDICINES = new ArrayList<>();

    static {
        // Initialize Sample Patients & Medical Histories
        PATIENTS.add(new PatientRecord("P101", "John Doe", 54, 
            "PATIENT DIAGNOSIS: ACUTE MYOCARDIAL INFARCTION DETECTED. HIGH CARDIOVASCULAR RISK.", 9.5, Arrays.asList("ICD-I21.9", "ICD-I10")));
        PATIENTS.add(new PatientRecord("P102", "Sarah Smith", 42, 
            "PATIENT HISTORY: TYPE 2 DIABETES MELLITUS WITH NEPHROPATHY COMPLICATION.", 7.8, Arrays.asList("ICD-E11.9")));
        PATIENTS.add(new PatientRecord("P103", "Robert Chen", 68, 
            "PATIENT DIAGNOSIS: CHRONIC BRONCHIAL ASTHMA WITH SEVERE COUGH AND FEVER.", 8.2, Arrays.asList("ICD-J45.9")));
        PATIENTS.add(new PatientRecord("P104", "Emily Davis", 31, 
            "PATIENT DIAGNOSIS: ACUTE PNEUMONIA AND HIGH FEVER SYMPTOMS.", 6.5, Arrays.asList("ICD-J18.9")));

        // Initialize Sample Doctors & Specialized Departments
        DOCTORS.add(new Doctor("D201", "Dr. Alice Walker", "Cardiology", true));
        DOCTORS.add(new Doctor("D202", "Dr. Robert Taylor", "Neurology", true));
        DOCTORS.add(new Doctor("D203", "Dr. Elena Rostova", "Oncology", true));
        DOCTORS.add(new Doctor("D204", "Dr. Marcus Vance", "Genetics & Pathology", false));

        // Initialize Sample Laboratory & Diagnostic Reports
        LAB_REPORTS.add(new DiagnosticReport("R301", "P101", 
            "LAB REPORT: DIABETES_MELLITUS_TYPE_2_DIABETES_MELLITUS_COMPLICATION", "AGTCGAT"));
        LAB_REPORTS.add(new DiagnosticReport("R302", "P102", 
            "LAB REPORT: CARDIOLOGY_NEUROLOGY_CARDIOLOGY_PATIENT_CHART", "AGCGAT"));

        // Initialize Sample Medicines & Drug Catalog
        MEDICINES.addAll(Arrays.asList("Penicillin", "Amoxicillin", "Ibuprofen", "Paracetamol", "Metformin", "Aspirin"));
    }

    public static List<PatientRecord> getPatients() { return new ArrayList<>(PATIENTS); }
    public static List<Doctor> getDoctors() { return new ArrayList<>(DOCTORS); }
    public static List<DiagnosticReport> getLabReports() { return new ArrayList<>(LAB_REPORTS); }
    public static List<String> getMedicines() { return new ArrayList<>(MEDICINES); }

    public static void addPatient(PatientRecord p) {
        PATIENTS.add(p);
    }

    public static void addDoctor(Doctor d) {
        DOCTORS.add(d);
    }

    public static void addLabReport(DiagnosticReport r) {
        LAB_REPORTS.add(r);
    }

    public static PatientRecord findPatientById(String id) {
        return PATIENTS.stream().filter(p -> p.getPatientId().equalsIgnoreCase(id.trim())).findFirst().orElse(null);
    }

    public static List<PatientRecord> searchPatients(String query) {
        String q = query.toLowerCase().trim();
        return PATIENTS.stream()
                .filter(p -> p.getPatientId().toLowerCase().contains(q) || p.getName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public static boolean appendMedicalHistory(String patientId, String newNote) {
        PatientRecord p = findPatientById(patientId);
        if (p != null) {
            String updated = p.getMedicalHistory() + " [NEW NOTE]: " + newNote;
            p.setMedicalHistory(updated);
            return true;
        }
        return false;
    }

    // =========================================================================
    // MODULE 4: Network Flow Sample Factory
    // =========================================================================
    public static FlowNetwork createDefaultResourceNetwork() {
        // 6 Nodes:
        // 0: Central Medical Depot (Source)
        // 1: Transit Hub North
        // 2: Transit Hub South
        // 3: Emergency Ward Alpha
        // 4: Emergency Ward Beta
        // 5: Regional Critical ICU (Sink)
        FlowNetwork net = new FlowNetwork(6);
        net.setNodeName(0, "Central Medical Depot [Source]");
        net.setNodeName(1, "Transit Hub North");
        net.setNodeName(2, "Transit Hub South");
        net.setNodeName(3, "Emergency Ward Alpha");
        net.setNodeName(4, "Emergency Ward Beta");
        net.setNodeName(5, "Regional Critical ICU [Sink]");

        net.addEdge(0, 1, 16); // Depot -> Hub North (16 oxygen units)
        net.addEdge(0, 2, 13); // Depot -> Hub South (13 oxygen units)
        net.addEdge(1, 2, 10); // Hub North -> Hub South (cross connection 10)
        net.addEdge(1, 3, 12); // Hub North -> Ward Alpha (12)
        net.addEdge(2, 1, 4);  // Hub South -> Hub North (4)
        net.addEdge(2, 4, 14); // Hub South -> Ward Beta (14)
        net.addEdge(3, 2, 9);  // Ward Alpha -> Hub South (9)
        net.addEdge(3, 5, 20); // Ward Alpha -> Critical ICU (20)
        net.addEdge(4, 3, 7);  // Ward Beta -> Ward Alpha (7)
        net.addEdge(4, 5, 4);  // Ward Beta -> Critical ICU (4)
        return net;
    }

    // =========================================================================
    // MODULE 4: Bipartite Matching Sample Data
    // =========================================================================
    public static class BipartiteSampleData {
        public final List<String> doctors;
        public final List<String> cases;
        public final List<List<Integer>> compatibility;

        public BipartiteSampleData(List<String> doctors, List<String> cases, List<List<Integer>> compatibility) {
            this.doctors = doctors;
            this.cases = cases;
            this.compatibility = compatibility;
        }
    }

    public static BipartiteSampleData getDefaultDoctorAssignmentData() {
        List<String> docs = Arrays.asList(
                "Dr. Alice Walker (Cardiology)",
                "Dr. Robert Taylor (Neurology)",
                "Dr. Elena Rostova (Oncology)",
                "Dr. Marcus Vance (Pathology)"
        );
        List<String> cases = Arrays.asList(
                "Case 1: Acute Heart Attack (Cardiac)",
                "Case 2: Acute Ischemic Stroke (Neuro)",
                "Case 3: Malignant Tumor Biopsy (Onco)",
                "Case 4: Severe Arrhythmia (Cardiac)"
        );

        List<List<Integer>> compat = new ArrayList<>();
        // Dr. Alice (Cardiology) can treat Case 1 and Case 4
        compat.add(Arrays.asList(0, 3));
        // Dr. Robert (Neurology) can treat Case 2
        compat.add(Arrays.asList(1));
        // Dr. Elena (Oncology) can treat Case 3
        compat.add(Arrays.asList(2));
        // Dr. Marcus (Pathology) can treat Case 3
        compat.add(Arrays.asList(2));

        return new BipartiteSampleData(docs, cases, compat);
    }

    // =========================================================================
    // MODULE 5: SAT / 3-SAT Sample Formulas
    // =========================================================================
    public static List<Sat3SatSolver.Clause> getDefaultTreatmentSatClauses() {
        // Medical rules:
        // x1: Prescribe Broad-Spectrum Antibiotic
        // x2: Prescribe Anticoagulant (Blood Thinner)
        // x3: Prescribe Proton-Pump Inhibitor (Gastric Protector)
        // x4: Prescribe Corticosteroid
        // Clauses:
        // C1: (x1 ∨ x2 ∨ x3) -> At least one vital therapy must be administered
        // C2: (¬x1 ∨ ¬x4)    -> Cannot give Antibiotic and Steroid without protection
        // C3: (¬x2 ∨ x3)     -> If Blood Thinner is given, must give Gastric Protector
        // C4: (x2 ∨ x4 ∨ ¬x3)-> Balanced therapeutic regimen
        List<Sat3SatSolver.Clause> clauses = new ArrayList<>();
        clauses.add(new Sat3SatSolver.Clause(1, 2, 3));
        clauses.add(new Sat3SatSolver.Clause(-1, -4));
        clauses.add(new Sat3SatSolver.Clause(-2, 3));
        clauses.add(new Sat3SatSolver.Clause(2, 4, -3));
        return clauses;
    }

    public static Map<Integer, String> getTreatmentVarNames() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "Antibiotic (x1)");
        map.put(2, "Anticoagulant (x2)");
        map.put(3, "GastricProtector (x3)");
        map.put(4, "Corticosteroid (x4)");
        return map;
    }

    // =========================================================================
    // MODULE 5: Conflict Graph Sample Data
    // =========================================================================
    public static NpReductions.ConflictGraph getDefaultHospitalConflictGraph() {
        List<String> wards = Arrays.asList(
                "ICU-Zone-A", "ICU-Zone-B", "Emergency-Triage",
                "Operating-Theater-1", "Operating-Theater-2", "Radiology-Suite"
        );
        NpReductions.ConflictGraph g = new NpReductions.ConflictGraph(6, wards);
        // Add schedule/contagion conflict edges
        g.addEdge(0, 1); // ICU-A conflicts with ICU-B
        g.addEdge(0, 2); // ICU-A conflicts with Triage
        g.addEdge(1, 2); // ICU-B conflicts with Triage
        g.addEdge(1, 3); // ICU-B conflicts with OT-1
        g.addEdge(2, 4); // Triage conflicts with OT-2
        g.addEdge(3, 4); // OT-1 conflicts with OT-2
        g.addEdge(4, 5); // OT-2 conflicts with Radiology
        return g;
    }

    // =========================================================================
    // MODULE 6: Sample Continuous Observations Stream
    // =========================================================================
    public static List<ReservoirSampling.Observation> getDefaultContinuousObservations() {
        List<ReservoirSampling.Observation> obs = new ArrayList<>();
        obs.add(new ReservoirSampling.Observation("OBS-01", "P101", "HEART_RATE", 118.0, "bpm"));
        obs.add(new ReservoirSampling.Observation("OBS-02", "P101", "SPO2", 92.5, "%"));
        obs.add(new ReservoirSampling.Observation("OBS-03", "P102", "GLUCOSE", 185.0, "mg/dL"));
        obs.add(new ReservoirSampling.Observation("OBS-04", "P103", "RESP_RATE", 26.0, "bpm"));
        obs.add(new ReservoirSampling.Observation("OBS-05", "P104", "BODY_TEMP", 102.4, "°F"));
        obs.add(new ReservoirSampling.Observation("OBS-06", "P101", "BLOOD_PRESSURE", 145.0, "mmHg"));
        obs.add(new ReservoirSampling.Observation("OBS-07", "P102", "INSULIN_LEVEL", 14.2, "uIU/mL"));
        obs.add(new ReservoirSampling.Observation("OBS-08", "P103", "PEAK_FLOW", 320.0, "L/min"));
        obs.add(new ReservoirSampling.Observation("OBS-09", "P104", "WHITE_BLOOD_CELLS", 12400.0, "/uL"));
        obs.add(new ReservoirSampling.Observation("OBS-10", "P101", "TROPONIN_I", 1.8, "ng/mL"));
        return obs;
    }

    // =========================================================================
    // MODULE 7: Sample Patient Statistical Dosage Array
    // =========================================================================
    public static long[] getDefaultPatientDosageArray() {
        // Daily clinical dosage units administered across 8 observation slots
        return new long[]{15, 20, 10, 35, 25, 40, 30, 50};
    }
}
