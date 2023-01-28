package com.autouploader.bot.GUI;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JOptionPane;

public class windowListener implements WindowListener {

	@Override
	public void windowOpened(WindowEvent e) {
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if(e.getWindow() instanceof ChangeFolder && Application.getSettings().isValid()) {
			Application.changeFolderFrame.setVisible(false);
			Application.mainFrame.setVisible(true);
		} else if(e.getWindow() instanceof ChangeWebhook && Application.getSettings().isValid()) {
			Application.changeWebhookFrame.setVisible(false);
			Application.mainFrame.setVisible(true);
		} else if(e.getWindow() instanceof Application) {
			if(Application.mainFrame.btnStopRecording.isEnabled()) {
				String[] options = new String[] {"Send", "Don't Send"};
				int response = JOptionPane.showOptionDialog(null, "You were recording multiple files.\nFiles have not been sent anywhere!\nDo you wish to send them or not?", "Multiple Files Save", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
				
				if(response == 0) {
					Application.mainFrame.listener.stopRecording(false);
					if(Application.mainFrame.listener.sendLogsInBatch()) {
						Application.mainFrame.listener.restartRecordingFile();
					}
					System.exit(0);
				} else if(response == 1) {
					System.exit(0);
				}
			} else System.exit(0);
		} else System.exit(0);
	}

	@Override
	public void windowClosed(WindowEvent e) {
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}

}
