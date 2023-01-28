package com.autouploader.bot.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;

import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;

import com.autouploader.bot.Functionality.Constants;
import com.autouploader.bot.Functionality.LogsListener;
import com.autouploader.bot.Functionality.Settings;
import com.mashape.unirest.http.exceptions.UnirestException;


import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JScrollPane;

public class Application extends JFrame {

	private JPanel contentPane;
	private JTextField textField = new JTextField();
	private JTextField textField_1 = new JTextField();
	private JTextField textField_2 = new JTextField();
	private JTextField textField_3 = new JTextField();

	public static ChangeFolder changeFolderFrame = new ChangeFolder();
	public static ChangeWebhook changeWebhookFrame = new ChangeWebhook();
	public static Application mainFrame = new Application();
	
    private static Settings settings = new Settings();

    public static DefaultListModel<String> model;
    public LogsListener listener = new LogsListener();

    public JButton btnStopSingleRecording;
    public JButton btnStartSingleRecording;
    public JButton btnStopRecording;
    public JList<String> list;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
                    Settings.readSettings();

                    if(!Application.settings.isValid()) {
                        if(Application.settings.getFolderIsAdded() == false)
                            changeFolderFrame.setVisible(true);
                        else
                            changeWebhookFrame.setVisible(true);
                    } else {
                        mainFrame.setVisible(true);
                        mainFrame.validateTextFields();
                    }
				} catch (FileNotFoundException e) {
                    changeFolderFrame.setVisible(true);
                } 
			}
		});
	}

	public Application() {
        setResizable(false);
        setIconImage(Constants.image);
		setTitle("Discord Autouploader / GW2 Autouploader");
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		addWindowListener(new windowListener());
		setBounds(100, 100, 742, 548);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Absolute path to your logging folder:");
		lblNewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 11, 317, 22);
		contentPane.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setEditable(false);
		textField.setBounds(10, 34, 346, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
        model = new DefaultListModel<String>();

		list = new JList<String>(model);
		list.setBorder(new LineBorder(new Color(0, 0, 0)));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setBounds(10, 110, 709, 303);
		
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setBounds(10, 110, 709, 303);
		contentPane.add(scrollPane);
		
		JButton btnNewButton = new JButton("Start Multiple Recording");
		btnNewButton.setToolTipText("Start the recording of batch of logs...");
		btnNewButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnNewButton.setBounds(10, 424, 178, 34);
		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Application.mainFrame.disableAllButtons();
                Application.mainFrame.btnStopRecording.setEnabled(true);
                try {
                    System.out.println("Starting multiple-file recording at location: " + Application.settings.getPathToFolder() + "!");
                    listener.startRecording(LogsListener.TypeOfRecording.MULTIPLE);
                } catch (IOException | UnirestException | InterruptedException e1) {
                    System.out.println("There's been an error processing a log!");
                    e1.printStackTrace();
                }
			}
			
		});
		contentPane.add(btnNewButton);
		
		btnStopRecording = new JButton("Stop Multiple Recording");
		btnStopRecording.setToolTipText("Stop the recording of logs...");
		btnStopRecording.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnStopRecording.setBounds(10, 469, 178, 34);
        btnStopRecording.setEnabled(false);
		btnStopRecording.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Application.mainFrame.enableAllButtons();
                Application.mainFrame.btnStopSingleRecording.setEnabled(false);
                Application.mainFrame.btnStopRecording.setEnabled(false);
				
				listener.stopRecording(true);
				listener.sendLogsInBatch();

                System.out.println("Stopped recording multiple files from location: " + Application.settings.getPathToFolder() + "!");
			}
		});
		contentPane.add(btnStopRecording);
		
		JButton btnNewButton_1 = new JButton("Change webhook");
		btnNewButton_1.setToolTipText("Change the webhook where logs are uploading...");
		btnNewButton_1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnNewButton_1.setBounds(478, 65, 152, 23);
		btnNewButton_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeWebhookFrame.setVisible(true);
				mainFrame.setVisible(false);
			}
		});
		contentPane.add(btnNewButton_1);
		
		JLabel lblNewLabel_1 = new JLabel("Link to your discord server webhook:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNewLabel_1.setBounds(402, 11, 317, 22);
		contentPane.add(lblNewLabel_1);
		
		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setColumns(10);
		textField_1.setBounds(366, 34, 353, 20);
		contentPane.add(textField_1);
		
		btnStartSingleRecording = new JButton("Start Single Recording");
		btnStartSingleRecording.setToolTipText("Start the recording of logs...");
		btnStartSingleRecording.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnStartSingleRecording.setBounds(198, 424, 178, 34);
        btnStartSingleRecording.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.mainFrame.disableAllButtons();
                Application.mainFrame.btnStopSingleRecording.setEnabled(true);
                try {
                    System.out.println("Starting recording at location: " + Application.settings.getPathToFolder() + "!");
                    listener.startRecording(LogsListener.TypeOfRecording.SINGLE);
                } catch (IOException | UnirestException | InterruptedException e1) {
                    JOptionPane.showMessageDialog(Application.mainFrame, "Something went wrong with the recording. The log was not uploaded!", "Discord Autouploader / GW2 Autouploader", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
		contentPane.add(btnStartSingleRecording);
		
		btnStopSingleRecording = new JButton("Stop Single Recording");
		btnStopSingleRecording.setToolTipText("Stop the recording of logs...");
		btnStopSingleRecording.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnStopSingleRecording.setBounds(198, 469, 178, 34);
        btnStopSingleRecording.setEnabled(false);
        btnStopSingleRecording.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Application.mainFrame.enableAllButtons();
                Application.mainFrame.btnStopSingleRecording.setEnabled(false);
                Application.mainFrame.btnStopRecording.setEnabled(false);

                listener.stopRecording(true);

                System.out.println("Stopped recording single files from location: " + Application.settings.getPathToFolder() + "!");
            }
        });
		contentPane.add(btnStopSingleRecording);
		
		JButton btnNewButton_1_1 = new JButton("Change folder");
		btnNewButton_1_1.setToolTipText("Change the folder from which logs are being uploaded...");
		btnNewButton_1_1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnNewButton_1_1.setBounds(107, 65, 152, 23);
		btnNewButton_1_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeFolderFrame.setVisible(true);
				mainFrame.setVisible(false);
			}
		});
		contentPane.add(btnNewButton_1_1);
		
		JLabel lblLatestUploadedLog = new JLabel("Latest uploaded log:");
		lblLatestUploadedLog.setHorizontalAlignment(SwingConstants.CENTER);
		lblLatestUploadedLog.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblLatestUploadedLog.setBounds(402, 424, 317, 22);
		contentPane.add(lblLatestUploadedLog);
		
		textField_2 = new JTextField();
		textField_2.setEditable(false);
		textField_2.setColumns(10);
		textField_2.setBounds(402, 452, 317, 20);
        textField_2.setHorizontalAlignment(JTextField.CENTER);
		contentPane.add(textField_2);
		
		textField_3 = new JTextField();
		textField_3.setEditable(false);
		textField_3.setColumns(10);
        textField_3.setHorizontalAlignment(JTextField.CENTER);
		textField_3.setBounds(402, 477, 317, 20);
		contentPane.add(textField_3);
		
		for(Component component : this.getContentPane().getComponents()) {
			if(component instanceof JButton) {
				JButton button = (JButton) component;
				button.setBackground(new Color(59, 89, 182));
				button.setForeground(Color.WHITE);
				button.setFocusPainted(false);
			}
			
			if(component instanceof JLabel) {
				JLabel label = (JLabel) component;
				label.setFont(new Font("Times New Roman", Font.PLAIN, 14));
			}
			
			if(component instanceof JTextField) {
				JTextField field = (JTextField) component;
				field.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			}
		}
		
		setLocationRelativeTo(null);
	}

    private void disableAllButtons() {
        for(Component component : Application.mainFrame.getContentPane().getComponents()) {
            if(component instanceof JButton) {
                component.setEnabled(false);
            }
        }
    }

    private void enableAllButtons() {
        for(Component component : Application.mainFrame.getContentPane().getComponents()) {
            if(component instanceof JButton) {
                component.setEnabled(true);
            }
        }
    }

	public static Settings getSettings() {
		return Application.settings;
	}

	public static void setSettings(Settings settings) {
		Application.settings = settings;
	}

    public void validateTextFields() {
        try {
            Settings.readSettings();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Application.mainFrame.textField.setText(Application.settings.getPathToFolder());
        Application.mainFrame.textField_1.setText(Application.settings.getLinkToWebhook());
        Application.mainFrame.validate();
        Application.mainFrame.repaint();
    }

    public void changeLatestBossUpload(String fightName, String permalink, Boolean isSuccess) {
        Application.mainFrame.textField_2.setText(fightName);
        Application.mainFrame.textField_3.setText(permalink);

        model.add(0, "Boss: " + fightName + ", link: " + permalink);
        Application.mainFrame.list.getComponent(0).setForeground(isSuccess ? Color.GREEN : Color.RED);

        Application.mainFrame.validate();
        Application.mainFrame.repaint();
    }
}
