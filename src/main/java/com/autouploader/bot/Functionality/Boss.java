package com.autouploader.bot.Functionality;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.autouploader.bot.GUI.Application;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Boss {
    private String fightName;
    private String duration;
    private Boolean success;
    private Boolean logBossIsCm;
    private String startTime;
    private String endTime;
    private String wing;
    private String emoji;
    private String permalink;
    private String error;

    public Boss(JsonElement element) {

        JsonObject object = element.getAsJsonObject();

        try {
            permalink = object.get("permalink").getAsString();                   // Important
        } catch (NullPointerException e) {
            this.fightName = "Unidentified";
            this.duration = "Unidentified";
            this.success = false;
            this.logBossIsCm = false;
            this.startTime = "Unidentified";
            this.endTime = "Unidentified";
            this.wing = "Unidentified";
            this.emoji = "Unidentified";
            this.permalink = "Unidentified";
            this.error = object.get("error").getAsString();
            return;
        }

        error = null;

        fightName = object.get("encounter").getAsJsonObject().get("boss").getAsString();

        Integer seconds = (int)Math.floor(object.get("encounter").getAsJsonObject().get("duration").getAsFloat());

        if(seconds > 60) {
            Integer minutes = seconds / 60;
            seconds %= 60;
            duration = (minutes < 10 ? "0" + minutes : minutes) + "m " + (seconds < 10 ? "0" + seconds : seconds) + "s";
        } else {
            duration = "00m " + (seconds < 10 ? "0" + seconds : seconds) + "s";
        }

        SimpleDateFormat sdf;
        Instant instant;
        Date date;

        success = object.get("encounter").getAsJsonObject().get("success").getAsBoolean();
        logBossIsCm = object.get("encounter").getAsJsonObject().get("isCm").getAsBoolean();

        instant = Instant.ofEpochSecond((long) object.get("encounterTime").getAsInt());
        date = Date.from(instant);

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X");

        startTime = sdf.format(date);

        Integer durationInteger = object.get("encounter").getAsJsonObject().get("duration").getAsInt();

        instant = Instant.ofEpochSecond((long) object.get("encounterTime").getAsInt() + (long)durationInteger);
        date = Date.from(instant);

        endTime = sdf.format(date);

        String shortenedName = this.fightName.replace(" CM", "");
        this.emoji = Constants.listOfEmojisAndBosses.get(shortenedName);

        for(Map.Entry<String, ArrayList<String>> entry : Constants.listOfBossesAndWings.entrySet()) {
            if(entry.getValue().contains(shortenedName)) {
                this.wing = entry.getKey();
            }
        }

        if(this.wing == null) {
            this.wing = "Unidentified";
        }

        Application.mainFrame.changeLatestBossUpload(fightName, permalink, success);
    }

    public String getWing() {
        return this.wing;
    }

    public String getFightName() {
		return fightName;
	}

	public void setFightName(String fightName) {
		this.fightName = fightName;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Boolean getLogBossIsCm() {
		return logBossIsCm;
	}

	public void setLogBossIsCm(Boolean logBossIsCm) {
		this.logBossIsCm = logBossIsCm;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

    public String getErrorString() {
        return this.error;
    }

    @Override
    public String toString() {
        String str = "[" + fightName + ": " + permalink + " : time: " + duration + "; startTime: " + startTime + "; endTime: " + endTime + "; isCm: " + logBossIsCm + "]";
        return str;
    }
}
