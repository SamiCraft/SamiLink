package com.samifying.link.discord.commands;

import com.samifying.link.AppUtils;
import com.samifying.link.entity.Data;
import com.samifying.link.repository.DataRepository;
import com.samifying.link.discord.CommandModule;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class BanCommand implements GuildCommand {

    private final DataRepository repository;

    @Autowired
    public BanCommand(@NotNull CommandModule module, DataRepository repository) {
        module.registerCommand(this);
        this.repository = repository;

    }

    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        List<Member> mentioned = event.getMessage().getMentionedMembers();
        TextChannel channel = event.getChannel();
        User author = event.getAuthor();

        // Mentioned member
        if (!mentioned.isEmpty()) {
            helper(channel, author, mentioned.get(0).getUser());
            return;
        }

        // User by id
        if (args.length == 1) {
            channel.getJDA().retrieveUserById(args[0]).queue(subject -> helper(channel, author, subject));
            return;
        }

        // Command usage
        AppUtils.sendCommandUsage(channel, getTriggers().get(0) + " <user-id>");
    }

    @Override
    public String getDescription() {
        return "Bans a discord user and he/she's minecraft account";
    }

    @Override
    public List<String> getTriggers() {
        return Collections.singletonList("!mcban");
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }

    private void helper(TextChannel channel, User author, @NotNull User subject) {
        Optional<Data> opt = repository.findByDiscordId(subject.getId());
        if (opt.isEmpty()) {
            AppUtils.sendErrorMessage(channel, author, "User not found");
            return;
        }

        Data data = opt.get();
        if (data.getBannedBy() != null) {
            AppUtils.sendErrorMessage(channel, author, "User is already banned");
        }

        data.setBannedAt(LocalDateTime.now());
        data.setBannedBy(author.getId());
        repository.save(data);

        String msg = "User " + subject.getAsMention() + " successfully banned by " + author.getAsMention();
        AppUtils.sendInfoAndLog(channel, author, msg);
        AppUtils.sendMessageToUser(subject, Color.RED, "You have been banned from the minecraft server");
    }
}
