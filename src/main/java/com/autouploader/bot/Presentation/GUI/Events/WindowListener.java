package com.autouploader.bot.Presentation.GUI.Events;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JOptionPane;

import com.autouploader.bot.MainApp;
import com.autouploader.bot.Presentation.ChangeFolder;
import com.autouploader.bot.Presentation.ChangeWebhook;
import com.autouploader.bot.Presentation.MainWindow;

public class WindowListener extends WindowAdapter {

    @Override
    public void windowClosing(WindowEvent e) {
        if (e.getWindow() instanceof MainWindow) {
            handleClosingEvent();
        }

        if(e.getWindow() instanceof ChangeFolder) {
            System.exit(0);
        }

        if(e.getWindow() instanceof ChangeWebhook) {
            System.exit(0);
        }
    }

    private void handleClosingEvent() {
        if(!MainApp.mainFrame.IsRecording()) {
            closeApplication();
            return;
        }

        if (getResponse() == 0) {
            sendAndClose();
            return;
        } 

        closeApplication();
    }

    private int getResponse() {
        String[] options = {"Send", "Don't Send"};
        
        int response = JOptionPane.showOptionDialog(
                null,
                "You were recording multiple files.\nFiles have not been sent anywhere!\nDo you wish to send them or not?",
                "Multiple Files Save",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );
        return response;
    }

    private void sendAndClose() {
        MainApp.mainFrame.stopRecording(false);
        if (MainApp.mainFrame.sendLogsInBatch()) {
            MainApp.mainFrame.restartRecordingFile();
        }
        System.exit(0);
    }

    private void closeApplication() {
        System.exit(0);
    }
}
