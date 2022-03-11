package com.samifying.link.discord.commands;

import com.samifying.link.data.Data;
import com.samifying.link.data.DataRepository;
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
        User user = event.getAuthor();
        Optional<Data> optional = repository.findByDiscordId(user.getId());
        if (optional.isEmpty()) {
            channel.sendMessage("You are not verified").queue();
            return;
        }

        Data data = optional.get();
        if (data.getBannedBy() != null) {
            channel.sendMessage("Sorry, you can't do that (**You are banned on the server**)").queue();
            return;
        }
        repository.deleteById(data.getId());
        channel.sendMessage("Your verification has been successfully removed").queue();
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
