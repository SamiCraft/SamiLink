package com.samifying.link.discord.commands;

import com.samifying.link.AppUtils;
import com.samifying.link.discord.CommandModule;
import com.samifying.link.entity.Data;
import com.samifying.link.model.AccountModel;
import com.samifying.link.repository.DataRepository;
import com.samifying.link.service.MojangService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class WhoisCommand implements GuildCommand {

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
    private final Logger logger = LoggerFactory.getLogger(WhoisCommand.class);
    private final DataRepository repository;
    private final MojangService service;

    @Autowired
    public WhoisCommand(@NotNull CommandModule module, DataRepository repository, MojangService service) {
        module.registerCommand(this);
        this.repository = repository;
        this.service = service;
    }

    @Override
    public void execute(@NotNull GuildMessageReceivedEvent event, String[] args) {
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
        AppUtils.sendCommandUsage(channel, "!whois <user-id>");
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

    private void helper(TextChannel channel, @NotNull User user) {
        try {
            Optional<Data> optional = repository.findByDiscordId(user.getId());
            if (optional.isEmpty()) {
                channel.sendMessage("No verification data found").queue();
                return;
            }

            Data data = optional.get();
            AccountModel account = service.getAccountByUUID(data.getUuid());
            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(Color.ORANGE)
                    .setTitle(MarkdownUtil.bold("Verification data"))
                    .addField("User:", user.getAsTag(), false)
                    .addField("Username:", account.getName(), false)
                    .addField("Created at:", dateFormat.format(data.getCreatedAt()), false)
                    .setThumbnail(user.getEffectiveAvatarUrl())
                    .setImage("https://visage.surgeplay.com/bust/" + AppUtils.cleanUUID(data.getUuid()))
                    .setFooter(String.valueOf(data.getId()))
                    .setTimestamp(Instant.now());

            // User is banned
            if (data.getBannedBy() != null) {
                builder.setColor(Color.RED);
                builder.addField("Status:", "Banned", false);
            }

            channel.sendMessage("Following data was found")
                    .setEmbeds(builder.build()).queue();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            channel.sendMessage("Failed retrieving the minecraft username").queue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
