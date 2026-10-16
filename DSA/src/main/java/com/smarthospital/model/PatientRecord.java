package com.smarthospital.model;

import java.util.List;

public class PatientRecord {
    private String patientId;
    private String name;
    private int age;
    private String medicalHistory;
    private double urgencyScore;
    private List<String> diagnosticCodes;

    public PatientRecord() {}

    public PatientRecord(String patientId, String name, int age, String medicalHistory, double urgencyScore, List<String> diagnosticCodes) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.medicalHistory = medicalHistory;
        this.urgencyScore = urgencyScore;
        this.diagnosticCodes = diagnosticCodes;
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    public double getUrgencyScore() { return urgencyScore; }
    public void setUrgencyScore(double urgencyScore) { this.urgencyScore = urgencyScore; }

    public List<String> getDiagnosticCodes() { return diagnosticCodes; }
    public void setDiagnosticCodes(List<String> diagnosticCodes) { this.diagnosticCodes = diagnosticCodes; }

    @Override
    public String toString() {
        return "PatientRecord{" +
                "id='" + patientId + '\'' +
                ", name='" + name + '\'' +
                ", urgency=" + urgencyScore +
                '}';
    }
}
