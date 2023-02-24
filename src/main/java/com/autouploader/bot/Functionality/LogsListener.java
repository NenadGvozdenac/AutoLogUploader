package com.autouploader.bot.Functionality;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import com.autouploader.bot.GUI.Application;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedField;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedFooter;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle;

public class LogsListener implements Runnable {
    
    public WatchService watchService;
    public Thread thread;
    public boolean recordingStill;

    public enum TypeOfRecording {
        SINGLE, MULTIPLE, STOPPED
    };

    TypeOfRecording typeOfRecording;

    /**
     * 
     * @param typeOfRecording
     * @throws IOException
     * @throws UnirestException
     * @throws InterruptedException
     */
    public void startRecording(TypeOfRecording typeOfRecording) throws IOException, UnirestException, InterruptedException {
        this.typeOfRecording = typeOfRecording;
        if(recordingFileExists()) {
            String[] options = new String[] {"Restart Recording", "Continue Recording"};
			int response = JOptionPane.showOptionDialog(null, "You were recording before.\nDo you wish to restart and record a new session?\nDo you wish to continue your last session?", "Multiple Files Save", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            if(response == 0) {
                restartRecordingFile();
            }
        }

        thread = new Thread(this);
        thread.start();  
    }

    /**
     * 
     * @return
     */
    public boolean recordingFileExists() {
        File file = new File("./logRecording.json");
        return file.exists();
    }

    /**
     * 
     */
    public void restartRecordingFile() {
        File file = new File("./logRecording.json");
        file.delete();
    }

    /**
     * 
     * @param sendMessage
     */
    public void stopRecording(Boolean sendMessage) {
        typeOfRecording = TypeOfRecording.STOPPED;
        if(sendMessage) {
            sendMessageAboutStoppingRecording();
        }
        this.recordingStill = false;
        this.thread.interrupt();
    }

    /**
     * 
     */
    private void sendMessageAboutStoppingRecording() {
        Application.mainFrame.setVisible(false);
        JOptionPane.showMessageDialog(null, "You stopped the recording!", "Discord Autouploader / GW2 Autouploader", JOptionPane.INFORMATION_MESSAGE);
        Application.mainFrame.setVisible(true);
    }

    /**
     * 
     * @param start
     * @throws IOException
     */
    public void registerAll(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public void run() {
        try {
            WatchKey watchKey = null;
            watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(Application.getSettings().getPathToFolder());
            registerAll(path);
            this.recordingStill = true;

            while (this.recordingStill && (watchKey = watchService.take()) != null) {
                for (WatchEvent<?> event : watchKey.pollEvents()) {

                    Path dir = (Path)watchKey.watchable();
                    Path fullPath = dir.resolve(event.context().toString());

                    registerAll(path);

                    if(fullPath.toString().contains(".zevtc")) {
                        System.out.println("New log detected! Uploading " + fullPath + ".");
                        File file = fullPath.toFile();  // arcdps file

                        uploadFileToArcDps(file);
                        break;
                    }
                }

                watchKey.reset();
            }
        } catch (InterruptedException e) {
            System.out.println("For a reason.");
        } catch (IOException e1) {
            e1.printStackTrace();
        }    
    }

    /**
     * 
     * @param file
     */
    public void uploadFileToArcDps(File file) {
       try {
            HttpResponse<String> response = Unirest.post("https://dps.report/uploadContent?json=1&generator=ei")
                .field("file", file)
                .asString();

            JsonElement element = JsonParser.parseString(response.getBody());

            Boss boss = new Boss(element, this);

            if(this.typeOfRecording == TypeOfRecording.SINGLE) {
                sendSingleBossRecording(boss);
            } else if(this.typeOfRecording == TypeOfRecording.MULTIPLE) {
                sendBossRecordingToJsonFile(boss);
            }
            
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param boss
     */
    private void sendBossRecordingToJsonFile(Boss boss) {
        Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setPrettyPrinting()
            .serializeNulls()
            .create();

        try {
            FileReader reader = new FileReader(new File("./logRecording.json"));

            Type founderTypeSet = new TypeToken<ArrayList<Boss>>(){}.getType();
            ArrayList<Boss> listOfBossesAlreadyRecorded = gson.fromJson(reader, founderTypeSet);

            listOfBossesAlreadyRecorded.add(boss);

            reader.close();

            try {
                FileWriter writer = new FileWriter(new File("./logRecording.json"));
                writer.write(gson.toJson(listOfBossesAlreadyRecorded));
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        } catch(FileNotFoundException e) {
            try {
                FileWriter writer = new FileWriter(new File("./logRecording.json"));

                ArrayList<Boss> listOfBossesAlreadyRecorded = new ArrayList<>();
                listOfBossesAlreadyRecorded.add(boss);

                writer.write(gson.toJson(listOfBossesAlreadyRecorded));
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch(IOException e2) {

        }
    }

    /**
     * 
     * @param boss
     */
    private void sendSingleBossRecording(Boss boss) {

        System.out.println("Sending " + boss.getFightName() + ", " + boss.getPermalink() + "!");

        WebhookClient client = new WebhookClientBuilder(Application.getSettings().getLinkToWebhook()).build();

        String bigString = "";

        bigString += (boss.getSuccess() == true ? ":white_check_mark:" : ":x:");
        bigString += " [" + boss.getFightName() + "](" + boss.getPermalink() + ")";
        bigString += " - " + boss.getDuration();

        client.send(bigString);
    }

    /**
     * 
     * @return
     */
    public boolean sendLogsInBatch() {

        Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setPrettyPrinting()
            .serializeNulls()
            .create();

        try (FileReader reader = new FileReader(new File("./logRecording.json"))) {
            Type founderTypeSet = new TypeToken<ArrayList<Boss>>(){}.getType();
            ArrayList<Boss> listOfBossesAlreadyRecorded = gson.fromJson(reader, founderTypeSet);

            reader.close();

            WebhookClient client = new WebhookClientBuilder(Application.getSettings().getLinkToWebhook()).build();
            WebhookEmbedBuilder builder = new WebhookEmbedBuilder();
            builder.setColor(Color.CYAN.getRGB());

            LinkedHashMap<String, ArrayList<Boss>> wings = new LinkedHashMap<String, ArrayList<Boss>>() {
                {
                    put("Spirit Vale", new ArrayList<>());
                    put("Salvation Pass", new ArrayList<>());
                    put("Stronghold of the Faithful", new ArrayList<>());
                    put("Bastion of the Penitent", new ArrayList<>());
                    put("Hall of Chains", new ArrayList<>());
                    put("Mythwright Gambit", new ArrayList<>());
                    put("The Key of Ahdashim", new ArrayList<>());
                    put("Practice Room", new ArrayList<>());
                    put("Icebrood Saga", new ArrayList<>());
                    put("End of Dragons", new ArrayList<>());
                    put("Nightmare", new ArrayList<>());
                    put("Shattered Observatory", new ArrayList<>());
                    put("Sunqua Peak", new ArrayList<>());
                    put("Unidentified", new ArrayList<>());
                }
            };

            for(int i = 0; i < listOfBossesAlreadyRecorded.size(); i++) {
                if(listOfBossesAlreadyRecorded.get(i).getErrorString() != null) {
                    listOfBossesAlreadyRecorded.remove(listOfBossesAlreadyRecorded.get(i));
                }
            }

            for(Boss boss : listOfBossesAlreadyRecorded) {
                if(wings.containsKey(boss.getWing())) {
                    wings.get(boss.getWing()).add(boss);
                }
            }

            ArrayList<Boss> failedBosses = new ArrayList<>();

            listOfBossesAlreadyRecorded.forEach(boss -> {
                if(boss.getSuccess() == false) {
                    failedBosses.add(boss);
                }
            });

            for(ArrayList<Boss> wing : new ArrayList<>(wings.values())) {
                if(wing.isEmpty()) continue;
                String bigString = "";
                boolean toAddField = false;
                String wingTitle = wing.get(0).getWing();

                for(Boss boss : wing) {
                    if(boss.getSuccess() == false) continue;
                    toAddField = true;
                    bigString += ((boss.getEmoji() == null) ? "" : boss.getEmoji() + " ") + "[" + boss.getFightName() + "](" + boss.getPermalink() + ") " + (boss.getDuration().contains("00m ") ? boss.getDuration().replace("00m ", "") : boss.getDuration())+ "\n";
                }

                if(toAddField) {
                    builder.addField(new EmbedField(false, wingTitle, bigString));
                }
            }
            builder.setFooter(new EmbedFooter("GW2 Log AutoUploader by NenadG", null));

            LocalDateTime ldt = LocalDateTime.now();
            ZoneId offset = ZoneOffset.systemDefault();
            OffsetDateTime odt = ZonedDateTime.of(ldt, offset).toOffsetDateTime();
    
            Boss firstBoss, lastBoss;

            try {
                firstBoss = listOfBossesAlreadyRecorded.get(0);
                lastBoss = listOfBossesAlreadyRecorded.get(listOfBossesAlreadyRecorded.size() - 1);
            } catch(IndexOutOfBoundsException e) {
                return false;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X", Locale.getDefault());

            long startMilis = sdf.parse(firstBoss.getStartTime()).getTime();
            long endMilis = sdf.parse(lastBoss.getEndTime()).getTime();

            long minutesTakenForClear = TimeUnit.MILLISECONDS.toMinutes(endMilis - startMilis);

            if(minutesTakenForClear > 500 || minutesTakenForClear < 0) {
                builder.setTitle(new EmbedTitle("Clear recorded.", null));
            } else {
                builder.setTitle(new EmbedTitle((minutesTakenForClear > 60 ? (minutesTakenForClear / 60) + " hour(s), " + (minutesTakenForClear % 60) + " minute(s) clear" : minutesTakenForClear + " minute(s) clear"), null));
            }

            builder.setTimestamp(odt);
            client.send(builder.build());

            if(!failedBosses.isEmpty()) {
                long secondsWiping = 0;
                String string = "";
    
                DateTimeFormatter f = DateTimeFormatter.ofPattern("HH'h' mm'm' ss's'");
    
                for(Boss boss : failedBosses) {
                    string += (
                        (boss.getEmoji() == null) ? "" : boss.getEmoji() + " ") 
                        + "[" + boss.getFightName() + "](" + boss.getPermalink() + ") " 
                        + (boss.getDuration().contains("00m ") ? boss.getDuration().replace("00m ", "") : boss.getDuration()) 
                    + "\n";
    
                    long bossWipeTime = LocalTime.parse("00h " + boss.getDuration(), f).toSecondOfDay();
                    secondsWiping += bossWipeTime;
                }
    
                long minutesWiping = secondsWiping / 60;
                secondsWiping %= 60;
    
                builder = new WebhookEmbedBuilder();
                builder.setColor(Color.CYAN.getRGB());
                builder.setTitle(new EmbedTitle(failedBosses.size() + " fail(s). Time taken: " + minutesWiping + " minute(s), " + secondsWiping + " second(s).", null));
                builder.setDescription(string);
                client.send(builder.build());
            }

            return true;
        } catch (IOException e) {
            System.out.println("No files were uploaded.");
            return false;
        } catch (ParseException e1) {
            JOptionPane.showMessageDialog(null, "Something went wrong with the recording.", "Discord Autouploader / GW2 Autouploader", JOptionPane.WARNING_MESSAGE);
            return false;
        } 
    }
}
