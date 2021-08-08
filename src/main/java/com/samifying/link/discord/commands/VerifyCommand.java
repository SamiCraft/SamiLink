package com.samifying.link.discord.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.samifying.link.AppUtils;
import com.samifying.link.data.Data;
import com.samifying.link.data.DataRepository;
import com.samifying.link.discord.CommandModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class VerifyCommand implements GuildCommand {

    private final Logger logger = LoggerFactory.getLogger(VerifyCommand.class);
    private final DataRepository repository;

    @Autowired
    public VerifyCommand(@NotNull CommandModule module, DataRepository repository) {
        module.registerCommand(this);
        this.repository = repository;
    }

    @Override
    public void execute(@NotNull GuildMessageReceivedEvent event, String[] args) {
        TextChannel channel = event.getChannel();
        Member member = event.getMember();
        if (member != null) {
            if (args.length == 1) {
                try {
                    JsonElement root = AppUtils.fetch("https://api.mojang.com/users/profiles/minecraft/" + args[0]);
                    JsonObject obj = root.getAsJsonObject();
                    String username = obj.get("name").getAsString();
                    String uuid = obj.get("id").getAsString();

                    if (username != null && uuid != null) {
                        User user = event.getAuthor();
                        Optional<Data> optional = repository.findByUuid(uuid);
                        if (optional.isPresent()) {
                            Data data = optional.get();
                            if (data.getDiscordId().equals(user.getId())) {
                                String linked = AppUtils.fetchUsername(data.getUuid());
                                dispatchWithData(channel, user, "You are already verified", linked, data.getUuid());
                                return;
                            }
                            if (data.getUuid().equals(uuid)) {
                                String message = "This minecraft account is already verified";
                                User linked = event.getJDA().getUserById(data.getDiscordId());
                                if (linked == null) {
                                    channel.sendMessage(message).queue();
                                    return;
                                }
                                dispatchWithData(channel, user, linked, message, username, data.getUuid());
                                return;
                            }
                        }

                        Data data = new Data();
                        data.setDiscordId(user.getId());
                        data.setUuid(uuid);
                        repository.save(data);
                        logger.info("User " + user.getAsTag() + " successfully verified");
                        String message = "You have successfully linked your minecraft account";
                        dispatchWithData(channel, user, message, username, uuid);
                    }
                } catch (IOException ex) {
                    channel.sendMessage("Username does not exist! You ether made a typo or Mojang API is down.").queue();
                    logger.error(ex.getMessage(), ex);
                }
            } else {
                channel.sendMessage("Command usage: `!verify <minecraft-username>`").queue();
            }
        } else {
            channel.sendMessage("You are not a server member").queue();
        }
    }

    @Override
    public String getDescription() {
        return "Links your Discord to a Minecraft account";
    }

    @Override
    public List<String> getTriggers() {
        return Arrays.asList("!verify", "!link");
    }

    @Override
    public boolean isAdminOnly() {
        return false;
    }

    private void dispatchWithData(@NotNull TextChannel channel, @NotNull User user, String message, String username, String uuid) {
        channel.sendMessage(user.getAsMention() + " " + message)
                .setEmbeds(new EmbedBuilder()
                        .setColor(Color.ORANGE)
                        .setTitle(MarkdownUtil.bold("Verification data"))
                        .addField("User:", user.getAsTag(), false)
                        .addField("Username:", username, false)
                        .setThumbnail(user.getEffectiveAvatarUrl())
                        .setImage("https://crafatar.com/renders/body/" + uuid)
                        .setTimestamp(Instant.now())
                        .build()).queue();
    }

    private void dispatchWithData(@NotNull TextChannel channel, @NotNull User user, @NotNull User subject, String message, String username, String uuid) {
        channel.sendMessage(user.getAsMention() + " " + message)
                .setEmbeds(new EmbedBuilder()
                        .setColor(Color.ORANGE)
                        .setTitle(MarkdownUtil.bold("Verification data"))
                        .addField("User:", subject.getAsTag(), false)
                        .addField("Username:", username, false)
                        .setThumbnail(subject.getEffectiveAvatarUrl())
                        .setImage("https://crafatar.com/renders/body/" + uuid)
                        .setTimestamp(Instant.now())
                        .build()).queue();
    }
}
