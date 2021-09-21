package com.samifying.link.data;

import com.samifying.link.AppConstants;
import com.samifying.link.discord.DiscordBot;
import com.samifying.link.error.LoginRejectedException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DataService {

    private final DataRepository repository;
    private final DiscordBot bot;

    @Autowired
    public DataService(DataRepository repository, DiscordBot bot) {
        this.repository = repository;
        this.bot = bot;
    }

    public ResponseEntity<UserData> getUserByUUID(String uuid) {
        Optional<Data> data = repository.findByUuid(uuid);
        if (data.isEmpty()) {
            throw new LoginRejectedException("You are not verified");
        }

        String id = data.get().getDiscordId();
        JDA jda = bot.getJda();
        Guild guild = jda.getGuildById(AppConstants.GUILD_ID);
        if (guild == null) {
            throw new LoginRejectedException("Discord server not found");
        }

        Member member = guild.retrieveMemberById(id).complete();
        if (member == null) {
            throw new LoginRejectedException("You are not a Discord server member");
        }

        String avatar = member.getUser().getEffectiveAvatarUrl();
        UserData ud = new UserData(member.getId(), member.getUser().getAsTag(), member.getEffectiveName(), avatar);

        // Check if the member is a supporter
        TextChannel supporter = guild.getTextChannelById(AppConstants.SUPPORTER_CHANNEL_ID);
        if (supporter != null && supporter.canTalk(member)) {
            // Member is a supporter
            ud.setSupporter(true);
        }

        // Check if the member is a server moderator
        Role staff = guild.getRoleById(AppConstants.STAFF_ROLE_ID);
        if (staff != null && member.getRoles().contains(staff)) {
            ud.setModerator(true);
        }

        // Check if its over level 5 or a supporter or staff
        Role role = guild.getRoleById(AppConstants.LEVEL_5_ROLE_ID);
        if (member.getRoles().contains(role) || ud.isSupporter() || ud.isModerator()) {
            return ResponseEntity.ok(ud);
        }
        throw new LoginRejectedException("You are not Level 5 on the Discord Server");
    }

    public Page<Data> getAllData(Pageable page) {
        return repository.findAll(page);
    }

    public ResponseEntity<Data> getDataById(int id) {
        Optional<Data> data = repository.findById(id);
        if (data.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(data.get());
    }

    public ResponseEntity<Data> getDataByDiscordId(String id) {
        Optional<Data> data = repository.findByDiscordId(id);
        if (data.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(data.get());
    }
}
