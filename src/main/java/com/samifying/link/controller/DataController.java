package com.samifying.link.controller;

import com.samifying.link.AppConstants;
import com.samifying.link.entity.Data;
import com.samifying.link.model.UserModel;
import com.samifying.link.service.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(path = "/api")
public class DataController {

    private final DataService service;

    @GetMapping(path = "/user/{uuid}")
    public UserModel getUser(
            @PathVariable String uuid,
            @RequestParam(required = false, defaultValue = "426156903555399680") Long role,
            @RequestParam(required = false, defaultValue = AppConstants.GUILD_ID) Long guild) {
        return service.getUserByUUID(uuid, role, guild);
    }

    @GetMapping(path = "/data")
    public Page<Data> getAll(Pageable page) {
        return service.getAllData(page);
    }

    @GetMapping(path = "/data/{id}")
    public ResponseEntity<Data> getById(@PathVariable int id) {
        return ResponseEntity.of(service.getDataById(id));
    }

    @GetMapping(path = "/data/discord/{id}")
    public ResponseEntity<Data> getByDiscordId(@PathVariable String id) {
        return ResponseEntity.of(service.getDataByDiscordId(id));
    }
}
