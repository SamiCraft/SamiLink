package com.samifying.link.controller;

import com.samifying.link.model.GuildModel;
import com.samifying.link.model.InfoModel;
import com.samifying.link.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(path = "/api/info")
public class InfoController {

    private final InfoService service;

    @GetMapping
    public InfoModel getInfo() {
        return service.getApplicationInfo();
    }

    @GetMapping(path = "/guilds")
    public List<GuildModel> getGuilds() {
        return service.getAllGuilds();
    }
}
