package com.smarthealth.logic;

import java.util.HashMap;
import java.util.Map;

/**
 * A rule-based symptom checker for basic medical suggestions.
 */
public class SymptomChecker {

    private final Map<String, String> diagnosisRules;
    private final GeminiClient geminiClient;

    public SymptomChecker() {
        diagnosisRules = new HashMap<>();
        initializeRules();
        this.geminiClient = new GeminiClient();
    }

    private void initializeRules() {
        // Simple rule-based logic fallback
        diagnosisRules.put("fever,cough", "Possible Flu (Influenza)");
        diagnosisRules.put("fever,cough,sore throat", "Possible Streptococcal Pharyngitis (Strep Throat)");
        diagnosisRules.put("cough", "Possible Common Cold or Viral Infection. Common accompanying symptoms: Fever, Sore Throat.");
        diagnosisRules.put("headache,nausea", "Possible Migraine");
        diagnosisRules.put("fatigue,shortness of breath", "Possible Anemia or Cardiac issue - Consult a Doctor");
        diagnosisRules.put("sneezing,runny nose,itchy eyes", "Possible Allergies");
        diagnosisRules.put("chest pain,sweating", "EMERGENCY: Possible Heart Attack - Call 911/Emergency Services");
        diagnosisRules.put("stomach pain,diarrhea", "Possible Food Poisoning or Viral Gastroenteritis");
        diagnosisRules.put("joint pain,stiffness", "Possible Arthritis");
    }

    /**
     * Checks symptoms using Gemini AI with a rule-based fallback.
     * @param symptoms Comma-separated symptoms
     * @return Possible diagnosis or AI analysis.
     */
    public String checkSymptoms(String symptoms) {
        if (symptoms == null || symptoms.trim().isEmpty()) {
            return "Please enter symptoms (e.g., fever, cough).";
        }

        // 1. Try Gemini API first (Modern AI approach)
        String aiDiagnosis = geminiClient.analyzeSymptoms(symptoms);
        
        // If the AI service returned an error, fallback to rule-based logic
        if (aiDiagnosis.startsWith("Error:") || aiDiagnosis.startsWith("Critical Error:")) {
            return fallbackCheck(symptoms);
        }

        return aiDiagnosis;
    }

    /**
     * Rule-based fallback if AI is unavailable.
     */
    private String fallbackCheck(String symptoms) {
        String input = symptoms.toLowerCase().replace(" ", "");
        
        // Check for matches in the rule map
        for (Map.Entry<String, String> entry : diagnosisRules.entrySet()) {
            String[] ruleSymptoms = entry.getKey().split(",");
            boolean allMatch = true;
            for (String s : ruleSymptoms) {
                if (!input.contains(s)) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) {
                return "[Fallback Analysis] " + entry.getValue();
            }
        }

        return "Condition not found in our database and AI service is currently unavailable. Please consult a qualified medical professional.";
    }
}
