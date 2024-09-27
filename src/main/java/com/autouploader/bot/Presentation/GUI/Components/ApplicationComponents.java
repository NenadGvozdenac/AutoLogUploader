package com.autouploader.bot.Presentation.GUI.Components;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import com.autouploader.bot.MainApp;
import com.autouploader.bot.Misc.Logger;
import com.autouploader.bot.Models.Boss;
import com.autouploader.bot.Models.LogsListener;
import com.autouploader.bot.Presentation.ChangeFolder;
import com.autouploader.bot.Presentation.ChangeWebhook;
import com.autouploader.bot.Presentation.MainWindow;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ApplicationComponents {
    private MainWindow mainFrame;
    private JTextField textField;
    private JTextField textField_1;
    private JTextField textField_2;
    private JTextField textField_3;
    private DefaultListModel<String> model;
    private JList<String> list;

    public LogsListener listener = new LogsListener();

    private JButton btnStopSingleRecording;
    private JButton btnStopRecording;
    private JButton btnChangeWebhook;
    private JButton btnStartMultipleRecording;
    private JButton btnChangeFolder;

    public ApplicationComponents(MainWindow mainFrame) {
        this.mainFrame = mainFrame;
        this.model = new DefaultListModel<>();
        this.list = new JList<>(model);
    }

    public void initializeComponents(JPanel contentPane) {
        setupComponents(contentPane);
        configureComponents();
    }

    private void setupComponents(JPanel contentPane) {
        JLabel lblLoggingFolder = createLabel("Absolute path to your logging folder:", 10, 11, 317, 22);
        contentPane.add(lblLoggingFolder);

        textField = createTextField(false, 10, 34, 346, 20);
        contentPane.add(textField);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBounds(10, 110, 709, 303);
        contentPane.add(scrollPane);

        JButton btnStartSingleRecording = createButton("Start Single Recording", 198, 424, e -> startSingleRecording());
        contentPane.add(btnStartSingleRecording);

        btnStopSingleRecording = createButton("Stop Single Recording", 198, 469, e -> stopSingleRecording());
        btnStopSingleRecording.setEnabled(false);
        contentPane.add(btnStopSingleRecording);

        btnStopRecording = createButton("Stop Multiple Recording", 10, 469, e -> stopMultipleRecording());
        btnStopRecording.setEnabled(false);
        contentPane.add(btnStopRecording);

        btnChangeWebhook = createButton("Change webhook", 478, 65, e -> invalidateWebhook());
        contentPane.add(btnChangeWebhook);

        JLabel lblWebhook = createLabel("Link to your discord server webhook:", 402, 11, 317, 22);
        contentPane.add(lblWebhook);

        textField_1 = createTextField(false, 366, 34, 353, 20);
        textField_1.setText(MainApp.settings.getLinkToWebhook());
        contentPane.add(textField_1);

        btnStartMultipleRecording = createButton("Start Multiple Recording", 10, 424, e -> startMultipleRecording());
        contentPane.add(btnStartMultipleRecording);

        btnChangeFolder = createButton("Change folder", 107, 65, e -> invalidateFolder());
        contentPane.add(btnChangeFolder);

        JLabel lblLatestUploadedLog = createLabel("Latest uploaded log:", 402, 424, 317, 22);
        contentPane.add(lblLatestUploadedLog);

        textField_2 = createTextField(false, 402, 452, 317, 20);
        textField_2.setHorizontalAlignment(JTextField.CENTER);
        contentPane.add(textField_2);

        textField_3 = createTextField(false, 402, 477, 317, 20);
        textField_3.setHorizontalAlignment(JTextField.CENTER);
        contentPane.add(textField_3);
    }

    private void invalidateFolder() {
        new ChangeFolder().setVisible(true);
        mainFrame.dispose();
    }

    private void invalidateWebhook() {
        new ChangeWebhook().setVisible(true);
        mainFrame.dispose();
    }

    private void configureComponents() {
        for (Component component : mainFrame.getContentPane().getComponents()) {
            if (component instanceof JButton) {
                configureButton((JButton) component);
            } else if (component instanceof JLabel) {
                configureLabel((JLabel) component);
            } else if (component instanceof JTextField) {
                configureTextField((JTextField) component);
            }
        }
    }

    private JLabel createLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(x, y, width, height);
        return label;
    }

    private JTextField createTextField(boolean editable, int x, int y, int width, int height) {
        JTextField textField = new JTextField();
        textField.setEditable(editable);
        textField.setColumns(10);
        textField.setBounds(x, y, width, height);
        return textField;
    }

    private JButton createButton(String text, int x, int y, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        button.setBounds(x, y, 178, 34);
        button.addActionListener(action);
        return button;
    }

    private void configureButton(JButton button) {
        button.setBackground(new Color(59, 89, 182));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    private void configureLabel(JLabel label) {
        label.setFont(new Font("Times New Roman", Font.PLAIN, 14));
    }

    private void configureTextField(JTextField field) {
        field.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(mainFrame, message, "Discord Autouploader / GW2 Autouploader", JOptionPane.WARNING_MESSAGE);
    }

    private void disableAllButtons() {
        for(Component component : mainFrame.getContentPane().getComponents()) {
            if(component instanceof JButton) {
                ((JButton) component).setEnabled(false);
            }
        }
    }

    private void enableAllButtons() {
        for(Component component : mainFrame.getContentPane().getComponents()) {
            if(component instanceof JButton) {
                ((JButton) component).setEnabled(true);
            }
        }
    }

    // Recording methods
    private void startSingleRecording() {
        disableAllButtons();
        btnStopSingleRecording.setEnabled(true);

        try {
            listener.startRecording(LogsListener.TypeOfRecording.SINGLE);
        } catch (IOException | UnirestException | InterruptedException e) {
            showErrorDialog("Something went wrong with the recording. The log was not uploaded!");
            Logger.log("Error while recording: " + e.getMessage());
        }
    }

    private void stopSingleRecording() {
        enableAllButtons();
        btnStopSingleRecording.setEnabled(false);
        btnStopRecording.setEnabled(false);

        listener.stopRecording(false);

        Logger.log("Single recording stopped!");
    }

    private void startMultipleRecording() {
        disableAllButtons();
        btnStopRecording.setEnabled(true);

        try {
            listener.startRecording(LogsListener.TypeOfRecording.MULTIPLE);
        } catch (IOException | UnirestException | InterruptedException e) {
            showErrorDialog("Something went wrong with the recording. The log was not uploaded!");
            Logger.log("Error while recording: " + e.getMessage());
        }
    }

    private void stopMultipleRecording() {
        enableAllButtons();
        btnStopRecording.setEnabled(false);
        btnStopSingleRecording.setEnabled(false);

        listener.stopRecording(false);

        Logger.log("Multiple recording stopped!");
    }

    public void updateTextFields() {
        textField.setText(MainApp.settings.getPathToFolder());
        textField_1.setText(MainApp.settings.getLinkToWebhook());
        mainFrame.validate();
        mainFrame.repaint();
    }

    public void changeLatestBossUpload(String fightName, String permalink, Boolean success) {
        textField_2.setText(fightName);
        textField_3.setText(permalink);
        textField_2.setBackground(success ? Color.GREEN : Color.RED);
        textField_3.setBackground(success ? Color.GREEN : Color.RED);
    }

    public void addBossToList(Boss boss) {
        model.addElement("Uploaded: " + boss.getFightName() + " - " + boss.getDuration() + " - " + (boss.getSuccess() ? "Success" : "Failure"));
    }
}
