package com.gw2.autouploader;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputListener;

import com.mashape.unirest.http.exceptions.UnirestException;

import java.awt.AWTException;
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
    public static JButton btnPersonalButtonStart;
    public static DefaultListModel<String> model;

    public static JFrame frame;

    public static TrayIcon icon;

    public static Integer activeRecording;

    MenuItem show = new MenuItem("Open");
    MenuItem exit = new MenuItem("Exit");
    MenuItem startRec = new MenuItem("Start Rec.");
    MenuItem stopRec = new MenuItem("Stop Rec.");
    MenuItem startPersonal = new MenuItem("Start Per. Rec.");

    public GUI(String pathToDir) throws AWTException {
        frame = this;

        setIconImage(new ImageIcon("icon.png").getImage());
		setTitle("Logging Application UI");
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setResizable(false);
		setBounds(100, 100, 450, 350);
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

        btnPersonalButtonStart = new JButton("Start Personal");                          // start recording button
		btnPersonalButtonStart.setToolTipText("Start the recording of logs...");
		btnPersonalButtonStart.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnPersonalButtonStart.setBounds(10, 280, 121, 23);
        btnPersonalButtonStart.addMouseListener(new ButtonEventListener());
        btnPersonalButtonStart.setEnabled(true);
		contentPane.add(btnPersonalButtonStart);
		
		btnNewButton_1 = new JButton("Change logging folder");                  // logging folder button
		btnNewButton_1.setToolTipText("Change the logs that are being uploaded...");
		btnNewButton_1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnNewButton_1.addMouseListener(new ButtonEventListener());
		btnNewButton_1.setBounds(141, 254, 152, 23);
		contentPane.add(btnNewButton_1);
		
		setLocationRelativeTo(null);

        if(SystemTray.isSupported() == true) {
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

            SystemTray tray = SystemTray.getSystemTray();

            PopupMenu menu = new PopupMenu();

            show.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.setVisible(true);
                }
            });

            exit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    System.exit(0);
                }
            });

            startRec.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        App.srv.startRecording(App.pathToDir, "http://78.108.218.94:25639/staticFileUpload");
                    } catch (IOException | UnirestException | InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    JButton currButton = btnNewButton;
                    currButton.setEnabled(false);
                    btnPersonalButtonStart.setEnabled(false);

                    activeRecording = 1;

                    GUI.btnStopRecording.setToolTipText("Static is active!");
   
                    startRec.setEnabled(false);
                    startPersonal.setEnabled(false);
                    stopRec.setEnabled(true);
                    GUI.btnStopRecording.setEnabled(true);
                }
            });

            startPersonal.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    btnPersonalButtonStart.setEnabled(false);
                    btnNewButton.setEnabled(false);

                    activeRecording = 2;

                    GUI.btnStopRecording.setToolTipText("Personal is active!");

                    startRec.setEnabled(false);
                    startPersonal.setEnabled(false);
                    stopRec.setEnabled(true);

                    GUI.btnStopRecording.setEnabled(true);
                }
                
            });

            stopRec.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    App.srv.stopRecording();

                    System.out.println(activeRecording + " is active.");
                    try {
                        HttpSrv.POST_STOP("http://78.108.218.94:25639/stopFileUpload", activeRecording);
                    } catch (UnirestException e1) {
                        e1.printStackTrace();
                    }

                    JButton currButton = (JButton) btnStopRecording;
                    currButton.setEnabled(false);
                    
                    stopRec.setEnabled(false);
                    startRec.setEnabled(true);
                    startPersonal.setEnabled(true);
                    GUI.btnNewButton.setEnabled(true);
                    GUI.btnPersonalButtonStart.setEnabled(true);
                }
            });

            stopRec.setEnabled(false);

            menu.add(show);
            menu.add(startRec);
            menu.add(startPersonal);
            menu.add(stopRec);
            menu.add(exit);

            icon = new TrayIcon(new ImageIcon("icon.png").getImage(), "GW2 Autouploader", menu);

            icon.setImageAutoSize(true);
            icon.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.setVisible(true);
                }
            });

            tray.add(icon);
        } else {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

        setVisible(true);
	}

    public class ButtonEventListener implements MouseInputListener {

        @Override
        public void mouseClicked(MouseEvent arg0) {
            
            if(arg0.getSource().equals(GUI.btnNewButton) && GUI.btnNewButton.isEnabled()) {
                try {
                    App.srv.startRecording(App.pathToDir, "http://78.108.218.94:25639/personalFileUpload");
                } catch (IOException | UnirestException | InterruptedException e) {
                    e.printStackTrace();
                }
                JButton currButton = (JButton) arg0.getSource();
                GUI.btnPersonalButtonStart.setEnabled(false);
                currButton.setEnabled(false);
                startRec.setEnabled(false);
                startPersonal.setEnabled(false);

                activeRecording = 1;
                GUI.btnStopRecording.setToolTipText("Static is active!");
                stopRec.setEnabled(true);
                GUI.btnStopRecording.setEnabled(true);
            } else if(arg0.getSource().equals(GUI.btnStopRecording) && GUI.btnStopRecording.isEnabled()) {
                App.srv.stopRecording();

                JButton currButton = (JButton) arg0.getSource();
                currButton.setEnabled(false);
                startRec.setEnabled(true);
                startPersonal.setEnabled(true);

                stopRec.setEnabled(false);
                GUI.btnNewButton.setEnabled(true);
                GUI.btnPersonalButtonStart.setEnabled(true);

                System.out.println(activeRecording + " is active.");
                try {
                    HttpSrv.POST_STOP("http://78.108.218.94:25639/stopFileUpload", activeRecording);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }
            } else if(arg0.getSource().equals(GUI.btnNewButton_1)) {
                GUI.frame.dispose();
                App.pathToDir = new DirPath().getNewDirectory();
                try {
                    new GUI(App.pathToDir);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
                App.srv = new HttpSrv();
            } else if(arg0.getSource().equals(GUI.btnPersonalButtonStart) && GUI.btnPersonalButtonStart.isEnabled()) {
                try {
                    App.srv.startRecording(App.pathToDir, "http://78.108.218.94:25639/personalFileUpload");
                } catch (IOException | UnirestException | InterruptedException e) {
                    e.printStackTrace();
                }
                activeRecording = 2;
                GUI.btnStopRecording.setToolTipText("Personal is active!");
                JButton currButton = (JButton) arg0.getSource();
                GUI.btnNewButton.setEnabled(false);
                currButton.setEnabled(false);
                startRec.setEnabled(false);
                startPersonal.setEnabled(false);
   
                stopRec.setEnabled(true);
                GUI.btnStopRecording.setEnabled(true);
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
