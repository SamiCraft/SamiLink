package com.samifying.link.discord.commands;

import com.samifying.link.AppUtils;
import com.samifying.link.discord.CommandModule;
import com.samifying.link.model.UserModel;
import com.samifying.link.service.LookupService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Component
public class LookupCommand implements GuildCommand {

    private final LookupService service;

    @Autowired
    public LookupCommand(CommandModule module, LookupService service) {
        module.registerCommand(this);
        this.service = service;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        TextChannel channel = event.getChannel();
        User author = event.getAuthor();

        if (args.length != 1) {
            AppUtils.sendCommandUsage(channel, getTriggers().get(0) + " <minecraft-username>");
            return;
        }

        UserModel model = service.findUserByMinecraftUsername(args[0]);
        channel.sendMessageEmbeds(new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle(MarkdownUtil.bold("Player lookup: " + args[0]))
                        .addField("Name:", model.getName(),false)
                        .addField("Nickname:",model.getNickname(),false)
                        .addField("Moderator:", String.valueOf(model.isModerator()),true)
                        .addField("Supporter:", String.valueOf(model.isSupporter()),true)
                        .setThumbnail(model.getAvatar())
                        .setFooter(author.getAsTag(), author.getEffectiveAvatarUrl())
                        .setTimestamp(Instant.now())
                .build()).queue();
    }

    @Override
    public String getDescription() {
        return "Finds and decodes user by Minecraft username";
    }

    @Override
    public List<String> getTriggers() {
        return Collections.singletonList("!lookup");
    }

    @Override
    public boolean isAdminOnly() {
        return false;
    }
}
