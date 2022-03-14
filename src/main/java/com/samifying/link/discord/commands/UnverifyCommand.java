package com.samifying.link.discord.commands;

import com.samifying.link.AppUtils;
import com.samifying.link.entity.Data;
import com.samifying.link.repository.DataRepository;
import com.samifying.link.discord.CommandModule;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class UnverifyCommand implements GuildCommand {

    private final DataRepository repository;

    @Autowired
    public UnverifyCommand(@NotNull CommandModule module, DataRepository repository) {
        module.registerCommand(this);
        this.repository = repository;
    }

    @Override
    public void execute(@NotNull GuildMessageReceivedEvent event, String[] args) {
        TextChannel channel = event.getChannel();
        User author = event.getAuthor();

        if (args.length != 0) {
            AppUtils.sendCommandUsage(channel, getTriggers().get(0));
            return;
        }

        Optional<Data> optional = repository.findByDiscordId(author.getId());
        if (optional.isEmpty()) {
            AppUtils.sendErrorMessage(channel, author, "You are not verified");
            return;
        }

        Data data = optional.get();
        if (data.getBannedBy() != null) {
            AppUtils.sendErrorMessage(channel, author, "Sorry, you can't do that (**You are banned on the server**)");
            return;
        }
        repository.deleteById(data.getId());
        AppUtils.sendInfoAndLog(channel, author, "Verification was successfully removed");
    }

    @Override
    public String getDescription() {
        return "Unlink your Minecraft account";
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("!unverify", "!unlink");
    }

    @Override
    public boolean isAdminOnly() {
        return false;
    }
}
