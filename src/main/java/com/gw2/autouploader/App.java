package com.gw2.autouploader;

import java.awt.AWTException;
import java.net.URISyntaxException;
import java.nio.file.WatchService;

import org.apache.http.client.ClientProtocolException;
import com.mashape.unirest.http.exceptions.UnirestException;

public class App  {

    public static String pathToDir = null;
    public static WatchService watchService;
    public static HttpSrv srv;

    public static void main( String[] args ) throws ClientProtocolException, UnirestException, InterruptedException, URISyntaxException, AWTException {

        pathToDir = new DirPath().getActiveDirectory();
        srv = new HttpSrv();
        new GUI(pathToDir);
    }
}
