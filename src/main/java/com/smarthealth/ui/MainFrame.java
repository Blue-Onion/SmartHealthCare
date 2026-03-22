package com.smarthealth.ui;

import com.smarthealth.logic.HealthReportGenerator;
import com.smarthealth.logic.SymptomChecker;
import com.smarthealth.model.Appointment;
import com.smarthealth.model.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Main UI Frame for the Smart Healthcare System.
 */
public class MainFrame extends JFrame {
    private List<Patient> patients = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();
    private SymptomChecker symptomChecker = new SymptomChecker();
    private HealthReportGenerator reportGenerator = new HealthReportGenerator();
    private Random random = new Random();

    private DefaultTableModel patientTableModel;
    private DefaultTableModel appointmentTableModel;
    private JTextArea symptomOutput;

    public MainFrame() {
        setTitle("Smart Healthcare System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Add Patient", createAddPatientPanel());
        tabbedPane.addTab("View Patients", createViewPatientsPanel());
        tabbedPane.addTab("Book Appointment", createBookAppointmentPanel());
        tabbedPane.addTab("View Appointments", createViewAppointmentsPanel());
        tabbedPane.addTab("Symptom Checker", createSymptomCheckerPanel());

        add(tabbedPane);
    }

    private JPanel createAddPatientPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField idField = new JTextField(generateRandomId());
        idField.setEditable(false);
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JButton addButton = new JButton("Add Patient");

        panel.add(new JLabel("Patient ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Gender:"));
        panel.add(genderBox);
        panel.add(new JLabel("")); // Placeholder
        panel.add(addButton);

        addButton.addActionListener(e -> {
            try {
                String id = idField.getText();
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String gender = (String) genderBox.getSelectedItem();

                if (id.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fieldsUID.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                patients.add(new Patient(id, name, age, gender));
                updatePatientTable();
                JOptionPane.showMessageDialog(this, "Patient added successfully!");
                
                // Clear fields and generate next ID
                idField.setText(generateRandomId());
                nameField.setText("");
                ageField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Age. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createViewPatientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Name", "Age", "Gender"};
        patientTableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(patientTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> updatePatientTable());
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBookAppointmentPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField pIdField = new JTextField();
        JTextField docField = new JTextField();
        JTextField dateField = new JTextField("YYYY-MM-DD");
        JButton bookButton = new JButton("Book Appointment");

        panel.add(new JLabel("Patient ID:"));
        panel.add(pIdField);
        panel.add(new JLabel("Doctor Name:"));
        panel.add(docField);
        panel.add(new JLabel("Date:"));
        panel.add(dateField);
        panel.add(new JLabel("")); // Placeholder
        panel.add(bookButton);

        bookButton.addActionListener(e -> {
            String pId = pIdField.getText();
            String doc = docField.getText();
            String date = dateField.getText();

            // Simple validation: check if patient exists
            boolean patientExists = patients.stream().anyMatch(p -> p.getId().equals(pId));
            if (!patientExists) {
                JOptionPane.showMessageDialog(this, "Patient ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            appointments.add(new Appointment(pId, doc, date));
            updateAppointmentTable();
            JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
        });

        return panel;
    }

    private JPanel createSymptomCheckerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField symptomField = new JTextField();
        JButton checkButton = new JButton("Check Symptoms");
        inputPanel.add(new JLabel("Enter symptoms (e.g., fever, cough): "), BorderLayout.NORTH);
        inputPanel.add(symptomField, BorderLayout.CENTER);
        inputPanel.add(checkButton, BorderLayout.EAST);

        panel.add(inputPanel, BorderLayout.NORTH);

        symptomOutput = new JTextArea();
        symptomOutput.setEditable(false);
        symptomOutput.setFont(new Font("Monospaced", Font.PLAIN, 14));
        panel.add(new JScrollPane(symptomOutput), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout());
        JTextField patientIdForPdf = new JTextField(10);
        JButton pdfButton = new JButton("Generate PDF Report");
        
        actionPanel.add(new JLabel("Patient ID for PDF:"));
        actionPanel.add(patientIdForPdf);
        actionPanel.add(pdfButton);
        panel.add(actionPanel, BorderLayout.SOUTH);

        checkButton.addActionListener(e -> {
            String result = symptomChecker.checkSymptoms(symptomField.getText());
            symptomOutput.setText("Result: " + result);
        });

        pdfButton.addActionListener(e -> {
            String pId = patientIdForPdf.getText();
            Patient patient = patients.stream()
                    .filter(p -> p.getId().equals(pId))
                    .findFirst()
                    .orElse(null);

            if (patient == null) {
                JOptionPane.showMessageDialog(this, "Patient not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String lastResult = symptomOutput.getText().replace("Result: ", "");
            if (lastResult.isEmpty()) {
                lastResult = "No recent symptom check performed.";
            }

            String message = reportGenerator.generateReport(patient, lastResult);
            JOptionPane.showMessageDialog(this, message);
        });

        return panel;
    }

    private void updatePatientTable() {
        patientTableModel.setRowCount(0);
        for (Patient p : patients) {
            patientTableModel.addRow(new Object[]{p.getId(), p.getName(), p.getAge(), p.getGender()});
        }
    }

    private JPanel createViewAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Patient ID", "Doctor Name", "Date"};
        appointmentTableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(appointmentTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> updateAppointmentTable());
        panel.add(refreshButton, BorderLayout.SOUTH);

        return panel;
    }

    private void updateAppointmentTable() {
        appointmentTableModel.setRowCount(0);
        for (Appointment a : appointments) {
            appointmentTableModel.addRow(new Object[]{a.getPatientId(), a.getDoctorName(), a.getDate()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }

    private String generateRandomId() {
        return "PID-" + (1000 + random.nextInt(9000));
    }
}
