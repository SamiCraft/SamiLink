package com.samifying.link.discord;

import com.samifying.link.AppConstants;
import com.samifying.link.AppUtils;
import com.samifying.link.discord.commands.GuildCommand;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class CommandModule extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(CommandModule.class);
    private final Set<GuildCommand> commands;

    @Autowired
    public CommandModule(@NotNull DiscordBot bot) {
        bot.registerListener(this);
        this.commands = new HashSet<>();
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        final User author = event.getAuthor();
        final Message message = event.getMessage();

        // Command can't be sent via system messages, bots or webhooks
        if (author.isBot() || author.isSystem() || message.isWebhookMessage()) {
            return;
        }


        String[] args = message.getContentRaw().trim().split("\\s+");
        commands.stream().filter(
                // Checking if message is a command
                command -> command.getTriggers().contains(args[0])
        ).findAny().ifPresent(command -> {
            try {
                String[] cmdArgs = Arrays.copyOfRange(args, 1, args.length);
                if (!command.isAdminOnly()) {
                    command.execute(event, cmdArgs);
                    return;
                }

                // Admin command must be executed in the correct server
                final Guild guild = event.getGuild();
                if (!guild.getId().equals(AppConstants.GUILD_ID)) {
                    return;
                }

                // Admin only commands can only be executed by staff
                final Member member = event.getMember();
                final Role role = guild.getRoleById(AppConstants.STAFF_ROLE_ID);
                if (member != null && role != null && member.getRoles().contains(role)) {
                    command.execute(event, cmdArgs);
                } else {
                    event.getChannel().sendMessage("You are not staff").queue();
                }
            } catch (Exception e) {
                AppUtils.sendErrorMessage(event.getChannel(), e, e.getMessage());
            }
        });
    }

    public synchronized void registerCommand(@NotNull GuildCommand command) {
        logger.info("Registering command " + command.getClass().getName());
        commands.add(command);
    }

    public synchronized Set<GuildCommand> getCommands() {
        return commands;
    }
}
