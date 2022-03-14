package com.samifying.link.discord.commands;

import com.pequla.server.ping.Player;
import com.pequla.server.ping.Players;
import com.pequla.server.ping.StatusResponse;
import com.samifying.link.AppConstants;
import com.samifying.link.discord.CommandModule;
import com.samifying.link.service.StatusService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StatusCommand implements GuildCommand {

    private final StatusService service;

    @Autowired
    public StatusCommand(@NotNull CommandModule module, StatusService service) {
        module.registerCommand(this);
        this.service = service;
    }

    @Override
    public void execute(@NotNull GuildMessageReceivedEvent event, String[] args) {
        TextChannel channel = event.getChannel();
        StatusResponse response = service.getDefaultServerStatus().getBody();
        if (response == null) {
            channel.sendMessage("Server is offline").queue();
            return;
        }
        Players players = response.getPlayers();
        channel.sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle(MarkdownUtil.bold("Minecraft server status"))
                .setThumbnail("https://api.mcsrvstat.us/icon/" + AppConstants.MINECRAFT_SERVER_ADDRESS)
                .addField("Version:", response.getVersion().getName(), true)
                .addField("Online:", players.getOnline() + "/" + players.getMax(), true)
                .addField("Players:", playerListFormat(players.getSample()), false)
                .setTimestamp(Instant.now())
                .build()).queue();
    }

    @Override
    public String getDescription() {
        return "Displays server status";
    }

    @Override
    public List<String> getTriggers() {
        return Collections.singletonList("!status");
    }

    @Override
    public boolean isAdminOnly() {
        return false;
    }

    private String playerListFormat(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return "Server is empty";
        }
        return players.stream().map(Player::getName).collect(Collectors.toList()).toString();
    }
}
