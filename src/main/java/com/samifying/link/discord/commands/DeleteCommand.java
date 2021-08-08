package com.samifying.link.discord.commands;

import com.samifying.link.data.Data;
import com.samifying.link.data.DataRepository;
import com.samifying.link.discord.CommandModule;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class DeleteCommand implements GuildCommand {

    private final DataRepository repository;

    @Autowired
    public DeleteCommand(CommandModule module, DataRepository repository) {
        module.registerCommand(this);
        this.repository = repository;

    }

    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        TextChannel channel = event.getChannel();
        if (args.length != 1) {
            channel.sendMessage("Command usage: `!delete <verification-id>`"
                    + System.lineSeparator()
                    + "You can find the verification id in the footer (ex: 36 o Today at 19:30) of the !whois/!find embed"
            ).queue();
            return;
        }

        try {
            Optional<Data> optional = repository.findById(Integer.valueOf(args[0]));
            if (optional.isPresent()) {
                repository.deleteById(optional.get().getId());
                channel.sendMessage("Verification under id " + args[0] + " was deleted").queue();
                return;
            }
            channel.sendMessage("Verification with that id was not found").queue();

        } catch (NumberFormatException ex) {
            channel.sendMessage("`" + args[0] + "` is not a number").queue();
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
