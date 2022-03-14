package com.samifying.link;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.awt.*;
import java.time.Instant;

public class AppUtils {

    public static void sendInfoMessage(TextChannel channel, User author, String message) {
        if (channel != null) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setTitle(MarkdownUtil.bold("Success"))
                    .setDescription(message)
                    .setFooter(author.getAsTag(), author.getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .build()).queue();
        }
    }

    public static void sendErrorMessage(TextChannel channel, User author, String message) {
        if (channel != null) {
            channel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle(MarkdownUtil.bold("Error"))
                    .setDescription(message)
                    .setFooter(author.getAsTag(), author.getEffectiveAvatarUrl())
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

    public static void sendInfoAndLog(TextChannel channel, User author, String message) {
        sendInfoMessage(channel, author, message);
        TextChannel log = channel.getJDA().getTextChannelById(AppConstants.LOGGING_CHANNEL_ID);
        if (log != null) {
            sendInfoMessage(log, author, message);
        }
    }

    public static void sendMessageToUser(User user, Color color, String message) {
        user.openPrivateChannel().queue(pc -> pc.sendMessageEmbeds(new EmbedBuilder()
                .setColor(color)
                .setTitle(MarkdownUtil.bold("System message"))
                .setDescription(MarkdownUtil.bold(message))
                .setTimestamp(Instant.now())
                .build()).queue());
    }
}
