package com.samifying.link.discord.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public interface GuildCommand {
    void execute(GuildMessageReceivedEvent event, String[] args);

    String getDescription();

    List<String> getTriggers();

    boolean isAdminOnly();
}
