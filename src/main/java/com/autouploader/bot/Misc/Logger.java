package com.autouploader.bot.Misc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public static final String LOG_FOLDER = "logs";

    public static void log(String message) {
        // Ensure the log folder exists
        File logDir = new File(LOG_FOLDER);
        if (!logDir.exists()) {
            logDir.mkdir();  // Create the log directory if it doesn't exist
        }

        // Create a timestamp for the log message
        String timeStamp = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
        message = "[" + timeStamp + "] " + message;

        // Create log file of today's date
        String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String logFileName = "log-" + today + ".txt";

        // Create log file in the logs folder
        String logFile = LOG_FOLDER + "/" + logFileName;

        // Use try-with-resources to automatically close resources
        try (FileWriter fileWriter = new FileWriter(logFile, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(message);
            System.out.println(message);  // Print to console for visibility
        } catch (IOException e) {
            e.printStackTrace();  // Handle exception by printing stack trace
        }
    }
}
