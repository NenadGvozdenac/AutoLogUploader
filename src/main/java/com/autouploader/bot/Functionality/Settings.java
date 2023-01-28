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

public class Settings {
	private Boolean folderIsAdded;
	private Boolean webhookIsAdded;
	private String pathToFolder;
	private String linkToWebhook;
	
	public Settings(Boolean folderIsAdded, Boolean webhookIsAdded, String pathToFolder, String linkToWebhook) {
		super();
		this.folderIsAdded = folderIsAdded;
		this.webhookIsAdded = webhookIsAdded;
		this.pathToFolder = pathToFolder;
		this.linkToWebhook = linkToWebhook;
	}
	
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

	public Boolean getFolderIsAdded() {
		return folderIsAdded;
	}
	
	public void setFolderIsAdded(Boolean folderIsAdded) {
		this.folderIsAdded = folderIsAdded;
	}
	
	public Boolean getWebhookIsAdded() {
		return webhookIsAdded;
	}
	
	public void setWebhookIsAdded(Boolean webhookIsAdded) {
		this.webhookIsAdded = webhookIsAdded;
	}
	
	public String getPathToFolder() {
		return pathToFolder;
	}
	
	public void setPathToFolder(String pathToFolder) {
		this.pathToFolder = pathToFolder;
	}
	
	public String getLinkToWebhook() {
		return linkToWebhook;
	}
	
	public void setLinkToWebhook(String linkToWebhook) {
		this.linkToWebhook = linkToWebhook;
	}

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

    public boolean isValid() {
        if(Application.getSettings().getFolderIsAdded() && Application.getSettings().getWebhookIsAdded()) {
            return true;
        } else return false;
    }
}
