package com.gw2.autouploader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputListener;

import com.mashape.unirest.http.exceptions.UnirestException;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Dialog.ModalExclusionType;

public class GUI extends JFrame {

    private JPanel contentPane;
	private JTextField textField;

    public static JButton btnNewButton, btnStopRecording, btnNewButton_1;
    public static DefaultListModel<String> model;

    public static JFrame frame;

    public GUI(String pathToDir) {
        frame = this;

        setIconImage(new ImageIcon("icon.png").getImage());
		setTitle("Logging Application UI");
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 331);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Absolute path to your logging folder:");
		lblNewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 11, 414, 22);
		contentPane.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setEditable(false);
        textField.setText(App.pathToDir);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		textField.setBounds(10, 34, 414, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblUploadedFilesAlready = new JLabel("Uploaded files already:");
		lblUploadedFilesAlready.setHorizontalAlignment(SwingConstants.CENTER);
		lblUploadedFilesAlready.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblUploadedFilesAlready.setBounds(10, 67, 414, 22);
		contentPane.add(lblUploadedFilesAlready);
		
        model = new DefaultListModel<>();

		JList<String> list = new JList<>(model);
		list.setMinimumSize(new Dimension(100, 100));
		list.setPreferredSize(new Dimension(300, 300));
		list.setSize(new Dimension(300, 300));
		list.setBorder(new LineBorder(new Color(0, 0, 0)));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setBounds(10, 93, 414, 150);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(list);
        list.setLayoutOrientation(JList.VERTICAL);
        scrollPane.setPreferredSize(new Dimension(300, 300));
        scrollPane.setBounds(10, 93, 414, 150);

		contentPane.add(scrollPane);
		
		btnNewButton = new JButton("Start Recording");                          // start recording button
		btnNewButton.setToolTipText("Start the recording of logs...");
		btnNewButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnNewButton.setBounds(10, 254, 121, 23);
        btnNewButton.addMouseListener(new ButtonEventListener());
        btnNewButton.setEnabled(true);
		contentPane.add(btnNewButton);
        
		btnStopRecording = new JButton("Stop recording");                       // stop recording button
		btnStopRecording.setToolTipText("Stop the recording of logs...");
		btnStopRecording.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnStopRecording.setBounds(303, 254, 121, 23);
        btnStopRecording.addMouseListener(new ButtonEventListener());
        btnStopRecording.setEnabled(false);
		contentPane.add(btnStopRecording);
		
		btnNewButton_1 = new JButton("Change logging folder");                  // logging folder button
		btnNewButton_1.setToolTipText("Change the logs that are being uploaded...");
		btnNewButton_1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnNewButton_1.addMouseListener(new ButtonEventListener());
		btnNewButton_1.setBounds(141, 254, 152, 23);
		contentPane.add(btnNewButton_1);
		
		setLocationRelativeTo(null);
        setVisible(true);
	}

    public class ButtonEventListener implements MouseInputListener {

        @Override
        public void mouseClicked(MouseEvent arg0) {
            
            if(arg0.getSource().equals(GUI.btnNewButton) && GUI.btnNewButton.isEnabled()) {
                try {
                    App.srv.startRecording(App.pathToDir);
                    JButton currButton = (JButton) arg0.getSource();
                    currButton.setEnabled(false);

                    GUI.btnStopRecording.setEnabled(true);
                } catch (IOException | InterruptedException | UnirestException e) {
                    e.printStackTrace();
                }
            } else if(arg0.getSource().equals(GUI.btnStopRecording) && GUI.btnStopRecording.isEnabled()) {
                App.srv.stopRecording();

                JButton currButton = (JButton) arg0.getSource();
                currButton.setEnabled(false);

                GUI.btnNewButton.setEnabled(true);
            } else if(arg0.getSource().equals(GUI.btnNewButton_1)) {
                GUI.frame.dispose();
                App.pathToDir = new DirPath().getNewDirectory();
                new GUI(App.pathToDir);
                App.srv = new HttpSrv();
            }
        }

        @Override
        public void mouseEntered(MouseEvent arg0) {
            
        }

        @Override
        public void mouseExited(MouseEvent arg0) {
            
        }

        @Override
        public void mousePressed(MouseEvent arg0) {
            
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
            
        }

        @Override
        public void mouseDragged(MouseEvent arg0) {
            
        }

        @Override
        public void mouseMoved(MouseEvent arg0) {
            
        }

    }

    public void CHOOSE_NEW_DIRECTORY() {
    }
}
