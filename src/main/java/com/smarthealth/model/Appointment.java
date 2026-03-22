package com.smarthealth.model;

/**
 * Represents an Appointment in the Smart Healthcare System.
 */
public class Appointment {
    private String patientId;
    private String doctorName;
    private String date;

    public Appointment(String patientId, String doctorName, String date) {
        this.patientId = patientId;
        this.doctorName = doctorName;
        this.date = date;
    }

    // Getters and Setters
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    @Override
    public String toString() {
        return "Patient ID: " + patientId + " | Doctor: " + doctorName + " | Date: " + date;
    }
}
