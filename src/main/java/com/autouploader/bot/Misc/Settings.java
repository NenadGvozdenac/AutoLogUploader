package com.autouploader.bot.Misc;

import java.io.Reader;
import java.io.Writer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
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

	private boolean haveBeenWritten = true;

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
    public static Settings readSettings() {
        try (Reader reader = new FileReader(new File("./path.json"))) {
			Gson gson = new GsonBuilder()
			    .disableHtmlEscaping()
			    .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
			    .setPrettyPrinting()
			    .serializeNulls()
			    .create();

			java.lang.reflect.Type founderTypeSet = new TypeToken<Settings>(){}.getType();
			return gson.fromJson(reader, founderTypeSet);
		} catch (JsonIOException | JsonSyntaxException | IOException e) {
			return null;
		}
    }

	/**
	 * 
	 * Function which, after successfully inputting new settings, writes new settings to path.json.
	 * 
	 * @param settings
	 * @throws IOException
	 */
    public static void writeSettings(Settings settings) {
        try (Writer writer = new FileWriter(new File("./path.json"))) {
			Gson gson = new GsonBuilder()
			    .disableHtmlEscaping()
			    .setFieldNamingStrategy(FieldNamingPolicy.UPPER_CAMEL_CASE)
			    .setPrettyPrinting()
			    .serializeNulls()
			    .create();

			writer.write(gson.toJson(settings));
			writer.close();

			settings.haveBeenWritten = true;
		} catch (IOException e) {
			Logger.log("Error writing settings to file: " + e.getMessage());
		}
    }

	public boolean folderExists() {
		return new File(pathToFolder).exists();
	}

    public boolean isValid() {
		return folderIsAdded && webhookIsAdded && folderExists();
    }

	public boolean haveBeenWritten() {
		return haveBeenWritten;
	}

    public void setHaveBeenWritten(boolean b) {
		this.haveBeenWritten = b;
    }
}
