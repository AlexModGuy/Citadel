package com.github.alexthe666.citadel.web;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class WebHelper {

    @Nullable
    public static BufferedReader getURLContents(String urlString, String backupFileLoc){
        BufferedReader reader = null;
        boolean useBackup = false;
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            url = null;
            useBackup = true;
        }
        if(url != null){
            URLConnection connection = null;
            try {
                connection = url.openConnection();
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
            } catch (IOException e) {
                e.printStackTrace();
                useBackup = true;
            }
        }
        if(useBackup){
            if (WebHelper.class.getClassLoader().getResourceAsStream(backupFileLoc) == null) {
                backupFileLoc = "assets/citadel/backup_text.txt";
            }
            try {
                reader = new BufferedReader(new InputStreamReader(WebHelper.class.getClass().getClassLoader().getResourceAsStream(backupFileLoc), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return reader;
    }

}
