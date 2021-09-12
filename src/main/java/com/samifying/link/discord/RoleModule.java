package com.samifying.link.discord;

import com.samifying.link.AppConstants;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleModule extends ListenerAdapter {

    @Autowired
    public RoleModule(@NotNull DiscordBot bot) {
        bot.registerListener(this);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Guild guild = event.getGuild();
        if (guild.getIdLong() != AppConstants.GUILD_ID) return;

        User author = event.getAuthor();
        Message message = event.getMessage();
        if (author.isBot() || author.isSystem() || message.isWebhookMessage()) return;

        List<Member> members = message.getMentionedMembers();
        if (members.isEmpty()) return;

        members.forEach(member -> {
            String content = message.getContentRaw().toLowerCase();
            if (member.getIdLong() == AppConstants.SAMIFYING_USER_ID && content.contains("peaches")) {
                member.getUser().openPrivateChannel().queue(
                        pc -> pc.sendMessage("Thanks for watching the video all the way through... as a thank you we've given you the \"YT MVP\" role but SHH... don't tell anyone how you got it <:sfyMal:885557699289440306>")
                                .queue());
                Role role = guild.getRoleById(AppConstants.CODEWORD_ROLE_ID);
                Member sender = event.getMember();
                TextChannel logs = guild.getTextChannelById(AppConstants.LOGGING_CHANNEL_ID);
                if (role != null && sender != null && logs != null) {
                    guild.addRoleToMember(sender, role).queue();
                    logs.sendMessageFormat("User **%s** (`%s`) guessed the codeword and got the role **%s** (`%s`)",
                            sender.getUser().getAsTag(), sender.getId(), role.getName(), role.getId()
                    ).queue();
                }
            }
        });
    }
}
