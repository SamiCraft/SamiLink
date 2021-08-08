package com.samifying.link.discord.commands;

import com.samifying.link.AppConstants;
import com.samifying.link.discord.CommandModule;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class HelpCommand implements GuildCommand {

    private final CommandModule module;

    @Autowired
    public HelpCommand(CommandModule module) {
        module.registerCommand(this);
        this.module = module;
    }

    @Override
    public void execute(@NotNull GuildMessageReceivedEvent event, String[] args) {
        StringBuilder builder = new StringBuilder();
        Member member = event.getMember();
        Role role = event.getGuild().getRoleById(AppConstants.STAFF_ROLE_ID);
        for (GuildCommand command : module.getCommands()) {
            if (member != null && role != null && command.isAdminOnly() && !member.getRoles().contains(role)) {
                continue;
            }
            builder.append(String.format("%-25s%-40s%n", command.getTriggers(), command.getDescription()));
        }
        event.getChannel().sendMessage(MarkdownUtil.bold("COMMAND LIST") +
                System.lineSeparator() +
                MarkdownUtil.codeblock(builder.toString())
        ).queue();
        event.getMessage().delete().completeAfter(2, TimeUnit.SECONDS);
    }

    @Override
    public String getDescription() {
        return "Displays a command list";
    }

    @Override
    public List<String> getTriggers() {
        return Collections.singletonList("!help");
    }

    @Override
    public boolean isAdminOnly() {
        return false;
    }
}
