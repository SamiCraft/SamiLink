package com.samifying.link.discord.commands;

import com.samifying.link.AppUtils;
import com.samifying.link.service.MojangService;
import com.samifying.link.entity.Data;
import com.samifying.link.repository.DataRepository;
import com.samifying.link.discord.CommandModule;
import com.samifying.link.error.MojangException;
import com.samifying.link.model.AccountModel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jetbrains.annotations.NotNull;
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

    private final DataRepository repository;
    private final MojangService service;

    @Autowired
    public VerifyCommand(@NotNull CommandModule module, DataRepository repository, MojangService service) {
        module.registerCommand(this);
        this.repository = repository;
        this.service = service;
    }

    @Override
    public void execute(@NotNull GuildMessageReceivedEvent event, String[] args) {
        TextChannel channel = event.getChannel();
        User author = event.getAuthor();
        if (args.length != 1) {
            AppUtils.sendCommandUsage(channel, getTriggers().get(0) + " <minecraft-username>");
            return;
        }
        try {
            AccountModel account = service.getAccountByUsername(args[0]);
            Optional<Data> optional = repository.findByUuid(account.getId());
            if (optional.isPresent()) {
                Data data = optional.get();
                if (data.getDiscordId().equals(author.getId())) {
                    // User has linked an account before
                    channel.sendMessage(author.getAsMention() + " You are already verified")
                            .setEmbeds(generateEmbed(author, service.getAccountByUUID(data.getUuid()))).queue();
                    return;
                }
                if (data.getUuid().equals(account.getId())) {
                    // User wants to link an account that has already been linked
                    event.getJDA().retrieveUserById(data.getDiscordId()).queue(user -> {
                        channel.sendMessage(author.getAsMention() + " This minecraft account is already verified")
                                .setEmbeds(generateEmbed(user, account)).queue();
                    });
                    return;
                }
            }

            Data data = new Data();
            data.setDiscordId(author.getId());
            data.setUuid(account.getId());
            data.setGuildId(event.getGuild().getId());
            repository.save(data);
            channel.sendMessage(author.getAsMention() + " You have successfully linked your minecraft account")
                    .setEmbeds(generateEmbed(author, account)).queue();
        } catch (MojangException we) {
            AppUtils.sendErrorMessage(channel, we, "Username does not exist");
        } catch (IOException | InterruptedException ex) {
            AppUtils.sendErrorMessage(channel, ex, "Could not connect to Mojang API");
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

    private MessageEmbed generateEmbed(User user, AccountModel account) {
        return new EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTitle(MarkdownUtil.bold("Verification data"))
                .addField("User:", user.getAsTag(), false)
                .addField("Username:", account.getName(), false)
                .setThumbnail(user.getEffectiveAvatarUrl())
                .setImage("https://visage.surgeplay.com/bust/" + AppUtils.cleanUUID(account.getId()))
                .setTimestamp(Instant.now())
                .build();
    }
}
