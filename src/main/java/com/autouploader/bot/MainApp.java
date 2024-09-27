package com.autouploader.bot;

import java.awt.EventQueue;

import com.autouploader.bot.Misc.ApplicationType;
import com.autouploader.bot.Misc.Logger;
import com.autouploader.bot.Misc.Settings;
import com.autouploader.bot.Presentation.ChangeFolder;
import com.autouploader.bot.Presentation.ChangeWebhook;
import com.autouploader.bot.Presentation.MainWindow;

public class MainApp {
    public static Settings settings;
    public static MainWindow mainFrame;

    public static ApplicationType applicationType;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Logger.log("Application started!");
            settings = Settings.readSettings() == null ? new Settings() : Settings.readSettings();
            handleApplicationStart();
        });
    }

    public static void handleApplicationStart() {
        if (!settings.isValid()) {
			fixSettings();
		} else if(settings.isValid() && !settings.haveBeenWritten()) {
            Settings.writeSettings(settings);
            
            mainFrame = new MainWindow();
			mainFrame.setVisible(true);
			mainFrame.validateTextFields();
        } else {
            mainFrame = new MainWindow();
			mainFrame.setVisible(true);
			mainFrame.validateTextFields();
		}
    }

    private static void fixSettings() {
        settings.setHaveBeenWritten(false);
        if(!(settings.getFolderIsAdded() && settings.folderExists())) {
            ChangeFolder changeFolderFrame = new ChangeFolder();
            changeFolderFrame.setVisible(true);
        } else if(!settings.getWebhookIsAdded()) {
            ChangeWebhook changeWebhookFrame = new ChangeWebhook();
            changeWebhookFrame.setVisible(true);
        }
    }
}
