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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.awt.TrayIcon.MessageType;

public class HttpSrv {
	
    public static Boolean recordingStill = false;

    public void startRecording(String pathToDir, String URL) throws IOException, UnirestException, InterruptedException {
        System.out.println("Started static listening...");

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
                                    DpsReportApi.UPLOAD_FILE(file, URL);
                                } catch (UnirestException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
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

    public static void POST_STOP(String URL, Integer activeRecording) throws UnirestException {
        
        // stopFileUpload

        String channelId = activeRecording == 1 ? "1007917782601572352" : "1027132780825559090";

        HttpResponse<String> response = Unirest.post(URL)
            .header("channelid", channelId).asString();

        System.out.println(response.getBody());

    }

    public static void POST(String fightname, String permalink, String duration, String success, String fightcm, String guiString, String startTime, String endTime, String URL) {

        Future<HttpResponse<String>> response;
        try {

            // String bossLogPermaLink = exchange.getRequestHeaders().getFirst("bosslog");
            // String bossLogTime = exchange.getRequestHeaders().getFirst("bosstime");
            // String bossLogSuccess = exchange.getRequestHeaders().getFirst("bosssuccess");
            response = Unirest.post(URL)
                .header("bosslog", permalink)
                .header("bosstime", duration)
                .header("bosssuccess", success)
                .header("bossname", fightname)
                .header("bossstart", startTime)
                .header("bossend", endTime)
                .header("bosscm", fightcm).asStringAsync();

            if(response.get().getStatus() == 500) {
                throw new UnirestException("Exception...");
            }

            GUI.icon.displayMessage("Log Upload Successful!", fightname + " was uploaded!", MessageType.INFO);

            GUI.model.addElement(guiString);
        } catch (UnirestException e) {
            JOptionPane.showMessageDialog(null, "Unfortunately, the port is not open to requests.", "ERROR", JOptionPane.ERROR_MESSAGE);
            App.srv.stopRecording();
            GUI.btnNewButton.setEnabled(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
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