package com.samifying.link.discord.commands;

import com.samifying.link.AppUtils;
import com.samifying.link.data.Data;
import com.samifying.link.data.DataRepository;
import com.samifying.link.discord.CommandModule;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class DeleteCommand implements GuildCommand {

    private final DataRepository repository;

    @Autowired
    public DeleteCommand(@NotNull CommandModule module, DataRepository repository) {
        module.registerCommand(this);
        this.repository = repository;

    }

    @Override
    public void execute(@NotNull GuildMessageReceivedEvent event, String @NotNull [] args) {
        TextChannel channel = event.getChannel();

        // Argument missing
        if (args.length != 1) {
            AppUtils.sendCommandUsage(channel, "!delete <data-id>");
            return;
        }

        try {
            Optional<Data> optional = repository.findById(Integer.valueOf(args[0]));
            if (optional.isEmpty()) {
                AppUtils.sendInfoMessage(channel, "No data found");
                return;
            }

            // Delete data
            repository.delete(optional.get());
            AppUtils.sendInfoMessage(channel, "Verification successfully deleted");
        } catch (NumberFormatException ex) {
            AppUtils.sendErrorMessage(channel, ex, args[0] + " is not a number");
        }
    }

    @Override
    public String getDescription() {
        return "Removes a link manually from a database";
    }

    @Override
    public List<String> getTriggers() {
        return Collections.singletonList("!delete");
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }
}
