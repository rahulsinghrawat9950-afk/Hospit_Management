package com.smarthospital.model;

public class DiagnosticReport {
    private String reportId;
    private String patientId;
    private String reportText;
    private String dnaSequence;

    public DiagnosticReport() {}

    public DiagnosticReport(String reportId, String patientId, String reportText, String dnaSequence) {
        this.reportId = reportId;
        this.patientId = patientId;
        this.reportText = reportText;
        this.dnaSequence = dnaSequence;
    }

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getReportText() { return reportText; }
    public void setReportText(String reportText) { this.reportText = reportText; }

    public String getDnaSequence() { return dnaSequence; }
    public void setDnaSequence(String dnaSequence) { this.dnaSequence = dnaSequence; }
}
