package com.gw2.autouploader;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.attribute.BasicFileAttributes;

import javax.swing.JOptionPane;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class HttpSrv {
	
    public static Boolean recordingStill = false;

    public void startRecording(String pathToDir) throws IOException, UnirestException, InterruptedException {
        System.out.println("Started listening...");

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    WatchKey watchKey;
                    App.watchService = FileSystems.getDefault().newWatchService();
                    Path path = Paths.get(pathToDir);
                    HttpSrv.registerAll(path);
                    recordingStill = true;

                    while ((watchKey = App.watchService.take()) != null && recordingStill) {
                        
                        for (WatchEvent<?> event : watchKey.pollEvents()) {

                            Path dir = (Path)watchKey.watchable();
                            Path fullPath = dir.resolve(event.context().toString());

                            HttpSrv.registerAll(path);

                            if(fullPath.toString().contains(".zevtc")) {
                                System.out.println("UPLOADING " + fullPath + "\n");
                                File file = fullPath.toFile();
                                try {
                                    DpsReportApi.UPLOAD_FILE(file);
                                } catch (UnirestException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                            
                            System.out.println(fullPath);
                        }
                        watchKey.reset();
                    }

                    System.err.println("Stopped");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void stopRecording() {
        if(recordingStill) {
            recordingStill = false;
            System.out.println("Stopped listening...");
        }
    }

    public static void POST(String finalString, String guiString) {
        String url = "http://78.108.218.94:25639/staticFileUpload";
        HttpResponse<String> response;
        try {
            response = Unirest.post(url).header("bosslog", finalString).asString();
            System.out.println(response.getBody());
            GUI.model.addElement(guiString);
        } catch (UnirestException e) {
            JOptionPane.showMessageDialog(null, "Unfortunately, the port is not open to requests.", "ERROR", JOptionPane.ERROR_MESSAGE);
            App.srv.stopRecording();
            GUI.btnNewButton.setEnabled(true);
        }
    }

    public static void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
    
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(App.watchService, StandardWatchEventKinds.ENTRY_CREATE);
                return FileVisitResult.CONTINUE;
            }
    
        });
    }
}