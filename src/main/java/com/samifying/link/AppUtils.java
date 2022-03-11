package com.samifying.link;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;

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

    public static void sendInfoMessage(TextChannel channel, String message) {
        if (channel != null) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.ORANGE)
                    .setDescription(message)
                    .setTimestamp(Instant.now())
                    .build()).queue();
        }
    }

    public static void sendErrorMessage(TextChannel channel, Exception e, String message) {
        if (channel != null) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle(MarkdownUtil.bold(e.getClass().getName()))
                    .setDescription(message)
                    .setTimestamp(Instant.now())
                    .build()).queue();
        }
    }

    public static void sendCommandUsage(TextChannel channel, String usage) {
        if (channel != null) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.BLUE)
                    .setTitle(MarkdownUtil.bold("Command usage"))
                    .setDescription(MarkdownUtil.codeblock(usage))
                    .setTimestamp(Instant.now())
                    .build()).queue();
        }
    }
}
