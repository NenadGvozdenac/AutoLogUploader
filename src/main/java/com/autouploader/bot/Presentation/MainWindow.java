package com.autouploader.bot.Presentation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.autouploader.bot.MainApp;
import com.autouploader.bot.Misc.Constants;
import com.autouploader.bot.Misc.Settings;
import com.autouploader.bot.Models.Boss;
import com.autouploader.bot.Presentation.GUI.Components.ApplicationComponents;
import com.autouploader.bot.Presentation.GUI.Events.WindowListener;

public class MainWindow extends JFrame {
    private JPanel contentPane;
    private ApplicationComponents components;

    public MainWindow() {
        setupFrame();
        components = new ApplicationComponents(this);
        components.initializeComponents(contentPane);
        setLocationRelativeTo(null);
    }

    private void setupFrame() {
        setResizable(false);
        setIconImage(Constants.image);
        setTitle("Discord Autouploader / GW2 Autouploader");
        setBounds(100, 100, 742, 548);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        addWindowListener(new WindowListener());
        setContentPane(contentPane);
        contentPane.setLayout(null);
    }

    public void validateTextFields() {
        MainApp.settings = Settings.readSettings();
        components.updateTextFields();
    }

    public void changeLatestBossUpload(String fightName, String permalink, Boolean success) {
        components.changeLatestBossUpload(fightName, permalink, success);
    }

    public void addBossToList(Boss boss) {
        components.addBossToList(boss);
    }

    public void stopRecording(boolean b) {
        components.listener.stopRecording(b);
    }

    public boolean sendLogsInBatch() {
        return components.listener.sendLogsInBatch();
    }

    public void restartRecordingFile() {
        components.listener.restartRecordingFile();
    }

    public boolean IsRecording() {
        return components.listener.recordingStill;
    }
}
