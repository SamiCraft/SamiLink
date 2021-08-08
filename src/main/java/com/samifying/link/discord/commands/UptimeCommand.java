package com.samifying.link.discord.commands;

import com.samifying.link.discord.CommandModule;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Collections;
import java.util.List;

@Component
public class UptimeCommand implements GuildCommand {

    @Autowired
    public UptimeCommand(@NotNull CommandModule module) {
        module.registerCommand(this);
    }

    @Override
    public void execute(@NotNull GuildMessageReceivedEvent event, String[] args) {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        long uptime = runtime.getUptime();
        long uptimeInSeconds = uptime / 1000;
        long numberOfHours = uptimeInSeconds / (60 * 60);
        long numberOfMinutes = (uptimeInSeconds / 60) - (numberOfHours * 60);
        long numberOfSeconds = uptimeInSeconds % 60;
        event.getChannel().sendMessageFormat(
                "My uptime is `%s hours, %s minutes, %s seconds`",
                numberOfHours, numberOfMinutes, numberOfSeconds
        ).queue();
    }

    @Override
    public String getDescription() {
        return "Returns bot (backend) online time";
    }

    @Override
    public List<String> getTriggers() {
        return Collections.singletonList("!uptime");
    }

    @Override
    public boolean isAdminOnly() {
        return false;
    }
}
