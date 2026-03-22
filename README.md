# Smart Healthcare System

A simple, Java-based healthcare management system with a graphical user interface (Swing), in-memory data storage, a rule-based symptom checker, and PDF report generation.

## Features

- **Patient Management**: Add and view patients (ID, Name, Age, Gender).
- **Appointment System**: Book appointments for registered patients.
- **Symptom Checker**: A "smart" rule-based system that suggests possible conditions based on symptoms.
- **PDF Report Generation**: Generate a professional PDF report containing patient details and the last diagnosis.

## Prerequisites

- Java Development Kit (JDK) 11 or higher.
- Apache Maven installed.

## How to Run

1. **Clone or Download** the project files into a folder.
2. **Navigate** to the project root directory (where `pom.xml` is located).
3. **Compile and Download Dependencies**:
   ```bash
   mvn clean install
   ```
4. **Run the Application**:
   ```bash
   mvn exec:java -Dexec.mainClass="com.smarthealth.ui.MainFrame"
   ```

## Code Structure

- `com.smarthealth.model`: Contains `Patient` and `Appointment` data classes.
- `com.smarthealth.logic`: Contains `SymptomChecker` (logic) and `HealthReportGenerator` (PDF logic).
- `com.smarthealth.ui`: Contains `MainFrame` (Swing UI).

## Usage Instructions

1. **Add Patient**: Go to the "Add Patient" tab, fill in the details, and click "Add Patient".
2. **View Patients**: Go to the "View Patients" tab and click "Refresh" to see the list.
3. **Book Appointment**: Use a valid Patient ID to book an appointment with a doctor.
4. **Symptom Checker**: 
   - Enter symptoms like "fever, cough" or "headache, nausea".
   - Click "Check Symptoms" to see the suggestion.
   - Enter the Patient ID and click "Generate PDF Report" to save a PDF file in the project root.
