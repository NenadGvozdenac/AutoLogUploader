package com.gw2.autouploader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class DirPath {
    
    public String path;

    public DirPath(String pathToDir) {
        this.path = pathToDir;
    }

    public DirPath() {

    }

    public String getPath() {
        return this.path;
    }

    public String getActiveDirectory() {

        String defaultPath = "C:\\Users\\matri\\OneDrive\\Documents\\Guild Wars 2\\addons\\arcdps\\arcdps.cbtlogs";

        try (Reader reader = new FileReader(new File("path.json"))) {

            Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .serializeNulls()
                .create();

            Type founderTypeSet = new TypeToken<DirPath>(){}.getType();
            DirPath commandData = gson.fromJson(reader, founderTypeSet);
            App.pathToDir = commandData.getPath();

            return App.pathToDir;
        } catch(IOException e) {
            Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .serializeNulls()
                .create();
        
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            fileChooser.setDialogTitle("Choose a directory.");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            fileChooser.setAcceptAllFileFilterUsed(false);

            Object[] options1 = {"Finished", "Cancel"};

            JPanel panel = new JPanel();
            panel.add(new JLabel("Choose a directory: "));
            panel.add(fileChooser);

            int result = JOptionPane.showOptionDialog(null, panel, "Choose a directory!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options1, null);

            if(result == JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Chosen directory " + fileChooser.getCurrentDirectory(), "CONFIRMATION", JOptionPane.PLAIN_MESSAGE);

                try (FileWriter writer = new FileWriter(new File("path.json"))) {
                    writer.write(gson.toJson(new DirPath(fileChooser.getCurrentDirectory().getAbsolutePath())));
                    writer.close();
    
                    defaultPath = fileChooser.getCurrentDirectory().getAbsolutePath();
                    return defaultPath;
                } catch(IOException e1) {
                    JOptionPane.showMessageDialog(null, "Unfortunately, path couldn't be read.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return defaultPath;
                } 
            } else {
                System.exit(0);
            }
        }
        return defaultPath;  
    }

    public String getNewDirectory() {

        String defaultPath = null;

        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .serializeNulls()
                .create();

        JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            fileChooser.setDialogTitle("Choose a directory.");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            fileChooser.setAcceptAllFileFilterUsed(false);

            Object[] options1 = {"Finished", "Cancel"};

            JPanel panel = new JPanel();
            panel.add(new JLabel("Choose a directory: "));
            panel.add(fileChooser);

            int result = JOptionPane.showOptionDialog(null, panel, "Choose a directory!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options1, null);

            if(result == JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(null, "Chosen directory " + fileChooser.getCurrentDirectory(), "CONFIRMATION", JOptionPane.PLAIN_MESSAGE);

                try (FileWriter writer = new FileWriter(new File("path.json"))) {
                    writer.write(gson.toJson(new DirPath(fileChooser.getCurrentDirectory().getAbsolutePath())));
                    writer.close();
    
                    defaultPath = fileChooser.getCurrentDirectory().getAbsolutePath();
                    return defaultPath;
                } catch(IOException e1) {
                    JOptionPane.showMessageDialog(null, "Unfortunately, path couldn't be read.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return defaultPath;
                } 
            } else {
                System.exit(0);
            }
            return defaultPath;
    }
}
