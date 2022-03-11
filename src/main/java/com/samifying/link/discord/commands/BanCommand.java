package com.samifying.link.discord.commands;

import com.samifying.link.AppConstants;
import com.samifying.link.AppUtils;
import com.samifying.link.data.Data;
import com.samifying.link.data.DataRepository;
import com.samifying.link.discord.CommandModule;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        TextChannel channel = event.getChannel();
        User author = event.getAuthor();

        // Argument missing
        if (args.length != 1) {
            AppUtils.sendCommandUsage(channel, "!mcban <data-id>");
            return;
        }

        try {
            Optional<Data> opt = repository.findById(Integer.valueOf(args[0]));
            if (opt.isEmpty()) {
                AppUtils.sendInfoMessage(channel, "No data found");
                return;
            }
            Data data = opt.get();
            data.setBannedAt(LocalDateTime.now());
            data.setBannedBy(author.getId());
            repository.save(data);

            JDA jda = event.getJDA();
            jda.retrieveUserById(data.getDiscordId()).queue(user -> {
                TextChannel logs = jda.getTextChannelById(AppConstants.LOGGING_CHANNEL_ID);
                String msg = "User " + user.getAsMention() + " successfully banned by " + author.getAsMention();
                AppUtils.sendInfoMessage(channel, msg);
                AppUtils.sendInfoMessage(logs, msg);
            });
        } catch (NumberFormatException e) {
            AppUtils.sendErrorMessage(channel, e, args[0] + " is not a number");
        }
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
}
