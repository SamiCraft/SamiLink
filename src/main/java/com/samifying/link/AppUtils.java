package com.samifying.link;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class AppUtils {
    public static JsonElement fetch(String link) throws IOException {
        URL url = new URL(link);
        URLConnection request = url.openConnection();
        request.connect();
        return JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
    }

    public static String fetchUsername(String uuid) throws IOException {
        JsonElement root = fetch("https://api.mojang.com/user/profiles/" + uuid + "/names");
        JsonArray array = root.getAsJsonArray();
        return array.get(array.size() - 1).getAsJsonObject().get("name").getAsString();
    }
}
