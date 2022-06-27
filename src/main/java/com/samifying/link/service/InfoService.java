package com.samifying.link.service;

import com.samifying.link.discord.DiscordBot;
import com.samifying.link.model.GuildModel;
import com.samifying.link.model.InfoModel;
import com.samifying.link.repository.DataRepository;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.SelfUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class InfoService {

    private final DataRepository repository;
    private final DiscordBot bot;

    public InfoModel getApplicationInfo() {
        SelfUser user = bot.getJda().getSelfUser();
        return InfoModel.builder()
                .id(user.getIdLong())
                .name(user.getAsTag())
                .serverCount(bot.getJda().getGuilds().size())
                .totalAccountLinks(repository.count())
                .build();
    }

    public List<GuildModel> getAllGuilds() {
        List<GuildModel> guilds = new ArrayList<>();
        bot.getJda().getGuilds().forEach(guild -> guilds.add(GuildModel.builder()
                .id(guild.getIdLong())
                .name(guild.getName())
                .iconUrl(guild.getIconUrl())
                .memberCount(guild.getMemberCount())
                .build()
        ));
        return guilds;
    }
}
