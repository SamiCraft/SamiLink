package com.samifying.link.discord.commands;

import com.samifying.link.AppUtils;
import com.samifying.link.data.Data;
import com.samifying.link.data.DataRepository;
import com.samifying.link.discord.CommandModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class WhoisCommand implements GuildCommand {

    private final Logger logger = LoggerFactory.getLogger(WhoisCommand.class);
    private final DataRepository repository;

    @Autowired
    public WhoisCommand(CommandModule module, DataRepository repository) {
        module.registerCommand(this);
        this.repository = repository;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        TextChannel channel = event.getChannel();
        User user = event.getAuthor();
        List<Member> mentioned = event.getMessage().getMentionedMembers();
        if (!mentioned.isEmpty()) {
            helper(channel, mentioned.get(0).getUser());
            return;
        }
        if (args.length == 0) {
            helper(channel, user);
            return;
        }
        if (args.length == 1) {
            event.getJDA().retrieveUserById(args[0]).queue(subject -> helper(channel, subject));
            return;
        }
        channel.sendMessage("Usage: `!whois` or `!whois <user-id>` or `!whois @username`").queue();
    }

    @Override
    public String getDescription() {
        return "Finds link data for the specified Discord user";
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("!whois", "!find");
    }

    @Override
    public boolean isAdminOnly() {
        return false;
    }

    private void helper(TextChannel channel, User user) {
        try {
            Optional<Data> optional = repository.findByDiscordId(user.getId());
            if (optional.isEmpty()) {
                channel.sendMessage("No verification data found").queue();
                return;
            }

            Data data = optional.get();
            String username = AppUtils.fetchUsername(data.getUuid());
            channel.sendMessage("Following data was found")
                    .setEmbeds(new EmbedBuilder()
                            .setColor(Color.ORANGE)
                            .setTitle(MarkdownUtil.bold("Verification data"))
                            .addField("User:", user.getAsTag(), false)
                            .addField("Username:", username, false)
                            .addField("Created at:", data.getCreatedAt().toString(), false)
                            .setThumbnail(user.getEffectiveAvatarUrl())
                            .setFooter(String.valueOf(data.getId()))
                            .setTimestamp(Instant.now())
                            .build()).queue();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            channel.sendMessage("Failed retrieving the minecraft username").queue();
        }
    }

}
