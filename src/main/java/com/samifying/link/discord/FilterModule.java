package com.samifying.link.discord;

import com.samifying.link.AppConstants;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterModule extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(FilterModule.class);

    @Autowired
    public FilterModule(DiscordBot bot) {
        bot.registerListener(this);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        if (channel.getIdLong() == AppConstants.TWITCH_CLIPS_CHANNEL_ID) {
            Member member = event.getMember();
            if (member != null && !member.getPermissions().contains(Permission.ADMINISTRATOR)) {
                User author = event.getAuthor();
                Message message = event.getMessage();
                String raw = message.getContentRaw();
                if (!raw.contains("https://clips.twitch.tv/")) {
                    logger.info("User " + author.getAsTag() + " has been warned!");
                    message.delete().queue();
                    author.openPrivateChannel().queue(
                            privateChannel -> privateChannel.sendMessage("This channel's messages ("
                                    + channel.getName()
                                    + ") can only contain links from **Twitch Clips** (https://clips.twitch.tv/)").queue()
                    );
                }
            }
        }
    }
}
