package com.autouploader.bot.Functionality;

import java.io.Reader;
import java.io.Writer;

import com.autouploader.bot.GUI.Application;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Settings class used for reading / writing the settings to path.json.
 */
public class Settings {
	private Boolean folderIsAdded;
	private Boolean webhookIsAdded;
	private String pathToFolder;
	private String linkToWebhook;

	/**
	 * Parameterised constructor for Settings class.
	 * @param folderIsAdded 	- boolean to indicate if the folder is added.
	 * @param webhookIsAdded 	- boolean to indicate if the webhook is added.
	 * @param pathToFolder 		- String path to the folder selected.
	 * @param linkToWebhook		- String link to the webhook selected.
	 */
	public Settings(Boolean folderIsAdded, Boolean webhookIsAdded, String pathToFolder, String linkToWebhook) {
		super();
		this.folderIsAdded = folderIsAdded;
		this.webhookIsAdded = webhookIsAdded;
		this.pathToFolder = pathToFolder;
		this.linkToWebhook = linkToWebhook;
	}
	
	/**
	 * Default constructor for Settings class.
	 */
	public Settings() {
		super();
		this.folderIsAdded = false;
		this.webhookIsAdded = false;
		this.pathToFolder = "";
		this.linkToWebhook = "";
	}

	@Override
	public String toString() {
		return "Settings [folderIsAdded=" + folderIsAdded + ", webhookIsAdded=" + webhookIsAdded + ", pathToFolder="
				+ pathToFolder + ", linkToWebhook=" + linkToWebhook + "]";
	}

	/**
	 * Getter for Boolean folderIsAdded;
	 * @return boolean 
	 */
	public Boolean getFolderIsAdded() {
		return folderIsAdded;
	}
	
	/**
	 * Setter for Boolean folderIsAdded
	 * @param folderIsAdded
	 */
	public void setFolderIsAdded(Boolean folderIsAdded) {
		this.folderIsAdded = folderIsAdded;
	}
	
	/**
	 * Getter for Boolean webhookIsAdded
	 * @return boolean
	 */
	public Boolean getWebhookIsAdded() {
		return webhookIsAdded;
	}
	
	/**
	 * Setter for Boolean webhookIsAdded
	 * @param webhookIsAdded
	 */
	public void setWebhookIsAdded(Boolean webhookIsAdded) {
		this.webhookIsAdded = webhookIsAdded;
	}
	
	/**
	 * Getter for String pathToFolder
	 * @return String
	 */
	public String getPathToFolder() {
		return pathToFolder;
	}

	/**
	 * Setter for String pathToFolder
	 * @param pathToFolder
	 */
	public void setPathToFolder(String pathToFolder) {
		this.pathToFolder = pathToFolder;
	}
	
	public String getLinkToWebhook() {
		return linkToWebhook;
	}
	
	public void setLinkToWebhook(String linkToWebhook) {
		this.linkToWebhook = linkToWebhook;
	}

	/**
	 * Function which reads the settings from the file path.json.
	 * @throws FileNotFoundException
	 */
    public static void readSettings() throws FileNotFoundException {
        Reader reader = new FileReader(new File("./path.json"));

        Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setPrettyPrinting()
            .serializeNulls()
            .create();

        java.lang.reflect.Type founderTypeSet = new TypeToken<Settings>(){}.getType();
        Application.setSettings(gson.fromJson(reader, founderTypeSet));
    }

	/**
	 * 
	 * Function which, after successfully inputting new settings, writes new settings to path.json.
	 * 
	 * @param settings
	 * @throws IOException
	 */
    public static void writeSettings(Settings settings) throws IOException {
        Writer writer = new FileWriter(new File("./path.json"));

        Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            .setPrettyPrinting()
            .serializeNulls()
            .create();

        writer.write(gson.toJson(settings));
        writer.close();
    }

	/**
	 * Function which returns whether the folder is added and webhook is set!
	 * @return boolean
	 */
    public boolean isValid() {
		return Application.getSettings().getFolderIsAdded() && Application.getSettings().getWebhookIsAdded();
    }
}
