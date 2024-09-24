package com.autouploader.bot.Presentation;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import com.autouploader.bot.MainApp;
import com.autouploader.bot.Misc.Constants;
import com.autouploader.bot.Misc.Logger;
import com.autouploader.bot.Presentation.GUI.Events.WindowListener;

public class ChangeFolder extends JFrame {
    private JPanel contentPane;
    private JTextField folderTextField;

    public ChangeFolder() {
        setupFrame();
        initializeComponents();
        setLocationRelativeTo(null);
    }

    private void setupFrame() {
        setResizable(false);
        setIconImage(Constants.image);
        setTitle("Discord Autouploader / GW2 Autouploader");
        setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 100, 530, 140);
        contentPane = new JPanel();
        addWindowListener(new WindowListener());
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
    }

    private void initializeComponents() {
        JLabel instructionLabel = createLabel("Change absolute path to your destinated folder (/arcdps.cbtlogs)", 10, 11, 499, 22);
        contentPane.add(instructionLabel);

        folderTextField = new JTextField();
        folderTextField.setBounds(10, 45, 499, 20);
        contentPane.add(folderTextField);

        JButton confirmButton = createButton("Confirm", 215, 76, 87, 23);
        confirmButton.addActionListener(new ConfirmButtonListener());
        contentPane.add(confirmButton);

        styleComponents();
    }

    private JLabel createLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(x, y, width, height);
        return label;
    }

    private JButton createButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        return button;
    }

    private void styleComponents() {
        for (Component component : contentPane.getComponents()) {
            if (component instanceof JLabel) {
                component.setFont(new Font("Times New Roman", Font.PLAIN, 14));
            }
            if (component instanceof JTextField) {
                ((JTextField) component).setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            }
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                button.setBackground(new Color(59, 89, 182));
                button.setForeground(Color.WHITE);
                button.setFocusPainted(false);
            }
        }
    }

    private class ConfirmButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Logger.log("Change folder initiated. New folder: " + folderTextField.getText());
            handleConfirmAction();
        }
    }

    private void handleConfirmAction() {
        String folderPath = folderTextField.getText();
        File directory = new File(folderPath);

        if (!directory.exists()) {
            JOptionPane.showMessageDialog(this, "That directory does not exist on this PC!",
                    "Discord Autouploader / GW2 Autouploader", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MainApp.settings.setFolderIsAdded(true);
        MainApp.settings.setPathToFolder(folderPath);

        MainApp.handleApplicationStart();

        dispose();
    }
}
