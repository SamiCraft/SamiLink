package com.samifying.link.service;

import com.samifying.link.AppConstants;
import com.samifying.link.discord.DiscordBot;
import com.samifying.link.entity.Data;
import com.samifying.link.error.LoginRejectedException;
import com.samifying.link.model.UserModel;
import com.samifying.link.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DataService {

    private final DataRepository repository;
    private final DiscordBot bot;

    public UserModel getUserByUUID(String uuid, Long roleId, Long guildId, Long supporterId, Long staffId) {
        Optional<Data> data = repository.findByUuid(uuid);
        if (data.isEmpty()) {
            throw new LoginRejectedException("You are not verified");
        }

        // Check if user is banned
        if (data.get().getBannedBy() != null) {
            throw new LoginRejectedException("You are banned from this server");
        }

        String id = data.get().getDiscordId();
        JDA jda = bot.getJda();
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            throw new LoginRejectedException("Discord server not found");
        }

        Member member = guild.retrieveMemberById(id).complete();
        if (member == null) {
            throw new LoginRejectedException("You are not a Discord server member");
        }

        String avatar = member.getUser().getEffectiveAvatarUrl();
        UserModel ud = new UserModel(member.getId(), member.getUser().getAsTag(), member.getEffectiveName(), avatar);

        // Check if the member is a supporter
        TextChannel supporter = guild.getTextChannelById(supporterId);
        if (supporter != null && supporter.canTalk(member)) {
            // Member is a supporter
            ud.setSupporter(true);
        }

        // Check if the member is a server moderator
        Role staff = guild.getRoleById(staffId);
        if (staff != null && member.getRoles().contains(staff)) {
            ud.setModerator(true);
        }

        // Check if player has required role or a supporter or staff
        Role role = guild.getRoleById(roleId);
        if (member.getRoles().contains(role) || ud.isSupporter() || ud.isModerator()) {
            return ud;
        }
        throw new LoginRejectedException("Required role: " + role.getName());
    }

    public Page<Data> getAllData(Pageable page) {
        return repository.findAll(page);
    }

    public Optional<Data> getDataById(int id) {
        return repository.findById(id);
    }

    public Optional<Data> getDataByDiscordId(String id) {
        return repository.findByDiscordId(id);
    }
}
