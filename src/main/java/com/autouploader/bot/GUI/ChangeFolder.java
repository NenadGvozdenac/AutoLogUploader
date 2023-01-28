package com.autouploader.bot.GUI;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import com.autouploader.bot.Functionality.Constants;
import com.autouploader.bot.Functionality.Settings;

import javax.swing.JButton;

public class ChangeFolder extends JFrame {
	private JPanel contentPane;
	private JTextField textField_1;
	public ChangeFolder() {
		
		setResizable(false);
        setIconImage(Constants.image);
		setTitle("Discord Autouploader / GW2 Autouploader");
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 530, 140);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		addWindowListener(new windowListener());
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Change absolute path to your destinated folder (/arcdps.cbtlogs)");
		lblNewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 11, 499, 22);
		contentPane.add(lblNewLabel);
		
		textField_1 = new JTextField();
		textField_1.setBounds(10, 45, 499, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnNewButton = new JButton("Confirm");
		btnNewButton.setBounds(215, 76, 87, 23);
		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
                String string = textField_1.getText();

				File directory = new File(string);
				if(!directory.exists()) {
					JOptionPane.showMessageDialog(Application.changeFolderFrame, "That directory does not exist on this PC!", "Discord Autouploader / GW2 Autouploader", JOptionPane.ERROR_MESSAGE);
					return;
				}

                Application.getSettings().setFolderIsAdded(true);
                Application.getSettings().setPathToFolder(string);
                try {
                    Settings.writeSettings(Application.getSettings());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
				Application.changeFolderFrame.setVisible(false);

                if(!Application.getSettings().isValid()) {
                    Application.changeWebhookFrame.setVisible(true);
                } else {
                    Application.mainFrame.setVisible(true);
                    Application.mainFrame.validateTextFields();
                }
			}
		});
		contentPane.add(btnNewButton);

		for(Component component : this.getContentPane().getComponents()) {
			if(component instanceof JLabel) {
				JLabel label = (JLabel) component;
				label.setFont(new Font("Times New Roman", Font.PLAIN, 14));
			}
			
			if(component instanceof JTextField) {
				JTextField field = (JTextField) component;
				field.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			}
			
			if(component instanceof JButton) {
				JButton button = (JButton) component;
				button.setBackground(new Color(59, 89, 182));
				button.setForeground(Color.WHITE);
				button.setFocusPainted(false);
			}
		}
		
		setLocationRelativeTo(null);
	}
}