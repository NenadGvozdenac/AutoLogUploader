package com.autouploader.bot.Models;

import java.awt.Color;
import java.io.File;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.autouploader.bot.MainApp;
import com.autouploader.bot.Misc.BossBuilder;
import com.autouploader.bot.Misc.Logger;
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

    public ConcurrentLinkedQueue<Boss> bosses = new ConcurrentLinkedQueue<>();

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
        if(typeOfRecording == TypeOfRecording.MULTIPLE) {
            sendLogsInBatch();
        }

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
        MainApp.mainFrame.setVisible(false);
        JOptionPane.showMessageDialog(null, "You stopped the recording!", "Discord Autouploader / GW2 Autouploader", JOptionPane.INFORMATION_MESSAGE);
        MainApp.mainFrame.setVisible(true);
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
            Path path = Paths.get(MainApp.settings.getPathToFolder());
            registerAll(path);
            this.recordingStill = true;

            while (this.recordingStill && (watchKey = watchService.take()) != null) {
                for (WatchEvent<?> event : watchKey.pollEvents()) {

                    Path dir = (Path)watchKey.watchable();
                    Path fullPath = dir.resolve(event.context().toString());

                    registerAll(path);

                    if(fullPath.toString().contains(".zevtc")) {
                        Logger.log("New .zevtc file detected: " + fullPath.toString());
                        File file = fullPath.toFile();  // arcdps file

                        uploadFileToArcDps(file);
                        break;
                    }
                }

                watchKey.reset();
            }
        } catch (InterruptedException e) {
            Logger.log("User stopped recording.");
        } catch (IOException e1) {
            Logger.log("Error while watching for new files: " + e1.getMessage());
        }    
    }

    /**
     * 
     * @param file
     */
    public void uploadFileToArcDps(File file) {
        CompletableFuture.runAsync(() -> {
            try {
                // Make POST request to upload the file
                HttpResponse<String> response = Unirest.post("https://dps.report/uploadContent?json=1&generator=ei&userToken=raaj0vg8gjvpc53i6mqaeb8ode4bp98r")
                    .field("file", file)
                    .asString();

                if(response.getStatus() != 200) {
                    Logger.log("Error uploading file to ArcDps: " + response.getStatusText());
                    return;
                }

                // Parse the response
                JsonElement element = JsonParser.parseString(response.getBody());
                Boss boss = new Boss(element, this);

                // Update bosses list on the main thread if needed
                synchronized (bosses) {
                    bosses.add(boss);
                }

                // Handle recording based on the type of recording
                processQueue(this.typeOfRecording);

                // Ensure UI updates occur on the main thread
                SwingUtilities.invokeLater(() -> {
                    MainApp.mainFrame.addBossToList(boss);
                });

            } catch (UnirestException e) {
                Logger.log("Error uploading file to ArcDps: " + e.getMessage());
            }
        });
    }

    private void processQueue(TypeOfRecording typeOfRecording2) {
        while(!bosses.isEmpty()) {
            Boss boss = bosses.poll();
            switch (typeOfRecording2) {
                case SINGLE:
                    sendSingleBossRecording(boss);
                    break;
                case MULTIPLE:
                    sendBossRecordingToJsonFile(boss);
                    break;
                default:
                    Logger.log("Unknown recording type.");
            }
        }
    }

    // Ensure thread safety with synchronized block
    private final Object fileLock = new Object();

    private void sendBossRecordingToJsonFile(Boss boss) {
        Gson gson = createGson();
    
        File logFile = new File("./logRecording.json");
    
        // Ensure thread safety with synchronized block
        synchronized (fileLock) {
            ArrayList<Boss> listOfBossesAlreadyRecorded = new ArrayList<>();

            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    Logger.log("Error creating logRecording.json: " + e.getMessage());
                }
            }
    
            try (FileReader reader = new FileReader(logFile)) {
                Type bossListType = new TypeToken<ArrayList<Boss>>() {}.getType();
                ArrayList<Boss> readType = gson.fromJson(reader, bossListType);
                listOfBossesAlreadyRecorded = readType == null ? new ArrayList<>() : readType;
            } catch (IOException e) {
                Logger.log("Error reading from logRecording.json: " + e.getMessage());
            }
    
            // Add the new boss to the list
            listOfBossesAlreadyRecorded.add(boss);
    
            // Attempt to write the updated list of bosses to the file
            try (FileWriter writer = new FileWriter(logFile)) {
                String writeType = gson.toJson(listOfBossesAlreadyRecorded);
                writer.write(writeType);
                Logger.log("Recording " + boss.getFightName() + ", " + boss.getPermalink() + " to the file.");
            } catch (IOException e) {
                Logger.log("Error writing to logRecording.json: " + e.getMessage());
            }
        }
    }

    private void sendSingleBossRecording(Boss boss) {
        Logger.log("Sending " + boss.getFightName() + ", `" + boss.getPermalink() + "`!");

        // Build the Webhook client
        try (WebhookClient client = new WebhookClientBuilder(MainApp.settings.getLinkToWebhook()).build()) {
            BossBuilder bossBuilder = new BossBuilder(boss);
            client.send(new StringBuilder().append(bossBuilder.build(typeOfRecording)).toString());
            Logger.log("Sent " + boss.getFightName() + ", `" + boss.getPermalink() + "`!");
        } catch (Exception e) {
            Logger.log("Error sending boss recording: " + e.getMessage());
        }
    }

    /**
     * 
     * @return
     */
    public boolean sendLogsInBatch() {
        Gson gson = createGson();
        
        ArrayList<Boss> listOfBossesAlreadyRecorded = readRecordedBosses(gson, "./logRecording.json");
        if (listOfBossesAlreadyRecorded == null) return false;

        WebhookClient client = new WebhookClientBuilder(MainApp.settings.getLinkToWebhook()).build();

        LinkedHashMap<String, ArrayList<Boss>> wings = initializeWings();

        filterBossesWithErrors(listOfBossesAlreadyRecorded);
        sortBossesByTime(listOfBossesAlreadyRecorded);
        organizeBossesByWing(listOfBossesAlreadyRecorded, wings);

        WebhookEmbedBuilder builder = new WebhookEmbedBuilder();
        builder.setColor(Color.CYAN.getRGB());

        addSuccessBossesToEmbed(builder, wings);
        builder.setFooter(new EmbedFooter("GW2 Log AutoUploader by NenadG", null));

        try {
            long minutesTakenForClear = calculateClearTime(listOfBossesAlreadyRecorded);
            setClearTimeTitle(builder, minutesTakenForClear);
        } catch (Exception e) {
            return false;
        }

        builder.setTimestamp(getCurrentTimestamp());
        client.send(builder.build());

        ArrayList<Boss> failedBosses = collectFailedBosses(listOfBossesAlreadyRecorded);
        if (!failedBosses.isEmpty()) {
            sendFailedBossesReport(client, failedBosses);
        }

        Logger.log("Sent logs in batch.");

        return true;
    }

    private void sortBossesByTime(ArrayList<Boss> listOfBossesAlreadyRecorded) {
        listOfBossesAlreadyRecorded.sort((b1, b2) -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X", Locale.getDefault());
                long b1Time = sdf.parse(b1.getStartTime()).getTime();
                long b2Time = sdf.parse(b2.getStartTime()).getTime();
                return Long.compare(b1Time, b2Time);
            } catch (ParseException e) {
                return 0;
            }
        });
    }

    private Gson createGson() {
        return new GsonBuilder()
            .disableHtmlEscaping()
            .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    }

    private ArrayList<Boss> readRecordedBosses(Gson gson, String filePath) {
        try (FileReader reader = new FileReader(new File(filePath))) {
            Type bossListType = new TypeToken<ArrayList<Boss>>(){}.getType();
            return gson.fromJson(reader, bossListType);
        } catch (IOException e) {
            System.out.println("No files were uploaded.");
            return null;
        }
    }

    private LinkedHashMap<String, ArrayList<Boss>> initializeWings() {
        return new LinkedHashMap<String, ArrayList<Boss>>() {
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
                put("Secrets of the Obscure", new ArrayList<>());
                put("Nightmare", new ArrayList<>());
                put("Shattered Observatory", new ArrayList<>());
                put("Sunqua Peak", new ArrayList<>());
                put("Unidentified", new ArrayList<>());
            }
        };
    }

    private void filterBossesWithErrors(ArrayList<Boss> listOfBosses) {
        listOfBosses.removeIf(boss -> boss.getErrorString() != null);
    }

    private void organizeBossesByWing(ArrayList<Boss> bosses, LinkedHashMap<String, ArrayList<Boss>> wings) {
        for (Boss boss : bosses) {
            if (wings.containsKey(boss.getWing())) {
                wings.get(boss.getWing()).add(boss);
            }
        }
    }

    private void addSuccessBossesToEmbed(WebhookEmbedBuilder builder, LinkedHashMap<String, ArrayList<Boss>> wings) {
        for (ArrayList<Boss> wing : wings.values()) {
            if (wing.isEmpty()) continue;

            StringBuilder wingContent = new StringBuilder();
            String wingTitle = wing.get(0).getWing();
            boolean hasContent = false;

            for (Boss boss : wing) {
                if (!boss.getSuccess()) continue;
                hasContent = true;

                BossBuilder bossBuilder = new BossBuilder(boss);
                wingContent.append(bossBuilder.build(typeOfRecording));
            }

            if (hasContent) {
                builder.addField(new EmbedField(false, wingTitle, wingContent.toString()));
            }
        }
    }

    private long calculateClearTime(ArrayList<Boss> listOfBossesAlreadyRecorded) throws ParseException {
        Boss firstBoss = listOfBossesAlreadyRecorded.get(0);
        Boss lastBoss = listOfBossesAlreadyRecorded.get(listOfBossesAlreadyRecorded.size() - 1);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X", Locale.getDefault());
        long startMillis = sdf.parse(firstBoss.getStartTime()).getTime();
        long endMillis = sdf.parse(lastBoss.getEndTime()).getTime();

        return TimeUnit.MILLISECONDS.toMinutes(endMillis - startMillis);
    }

    private void setClearTimeTitle(WebhookEmbedBuilder builder, long minutesTakenForClear) {
        if (minutesTakenForClear > 500 || minutesTakenForClear < 0) {
            builder.setTitle(new EmbedTitle("Clear recorded.", null));
        } else {
            String title = minutesTakenForClear > 60 
                ? (minutesTakenForClear / 60) + " hour(s), " + (minutesTakenForClear % 60) + " minute(s) clear"
                : minutesTakenForClear + " minute(s) clear";
            builder.setTitle(new EmbedTitle(title, null));
        }
    }

    private OffsetDateTime getCurrentTimestamp() {
        LocalDateTime ldt = LocalDateTime.now();
        ZoneId zoneId = ZoneOffset.systemDefault();
        return ZonedDateTime.of(ldt, zoneId).toOffsetDateTime();
    }

    private ArrayList<Boss> collectFailedBosses(ArrayList<Boss> bosses) {
        ArrayList<Boss> failedBosses = new ArrayList<>();
        for (Boss boss : bosses) {
            if (!boss.getSuccess()) {
                failedBosses.add(boss);
            }
        }
        return failedBosses;
    }

    private void sendFailedBossesReport(WebhookClient client, ArrayList<Boss> failedBosses) {
        WebhookEmbedBuilder builder = new WebhookEmbedBuilder();
        builder.setColor(Color.CYAN.getRGB());

        long totalWipeSeconds = 0;
        StringBuilder failReport = new StringBuilder();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH'h' mm'm' ss's'");
        
        for (Boss boss : failedBosses) {
            failReport.append((boss.getEmoji() != null ? boss.getEmoji() + " " : ""))
                    .append("[").append(boss.getFightName()).append("](")
                    .append(boss.getPermalink()).append(") ")
                    .append(boss.getDuration().replace("00m ", "")).append("\n");

            long wipeTime = LocalTime.parse("00h " + boss.getDuration(), formatter).toSecondOfDay();
            totalWipeSeconds += wipeTime;
        }

        long minutesWiping = totalWipeSeconds / 60;
        long secondsWiping = totalWipeSeconds % 60;

        builder.setTitle(new EmbedTitle(failedBosses.size() + " fail(s). Time taken: " 
            + minutesWiping + " minute(s), " + secondsWiping + " second(s).", null));
        builder.setDescription(failReport.toString());

        client.send(builder.build());
    }
}
