package com.gw2.autouploader;

import java.io.File;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DpsReportApi {

    public static void UPLOAD_FILE(File file, String URL) throws UnirestException {
        HttpResponse<String> response = Unirest.post("https://dps.report/uploadContent?json=1&generator=ei")
                .field("file", file)
                .asString();

        JsonElement element = JsonParser.parseString(response.getBody());

        JsonObject object = element.getAsJsonObject();
        String permalink = object.get("permalink").getAsString();                   // Important

        HttpResponse<String> responseDetailed = Unirest.get("https://dps.report/getJson")
                .queryString("permalink", permalink)
                .asString();

        JsonElement elementDetailed = JsonParser.parseString(responseDetailed.getBody());
        JsonObject objectDetailed = elementDetailed.getAsJsonObject();

        String fightName = objectDetailed.get("fightName").getAsString();
        String duration = objectDetailed.get("duration").getAsString();
        Boolean success = objectDetailed.get("success").getAsBoolean();
        Boolean logBossIsCm = object.getAsJsonObject().get("encounter").getAsJsonObject().get("isCm").getAsBoolean();
        String startTime = objectDetailed.get("timeStart").getAsString();
        String endTime = objectDetailed.get("timeEnd").getAsString();

        String guiString = fightName + " - " + permalink;

        HttpSrv.POST(fightName, permalink, duration, String.valueOf(success), String.valueOf(logBossIsCm), guiString, startTime, endTime, URL);
    }
}
