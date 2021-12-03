package com.samifying.link.data;

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
    public UserData getUser(@PathVariable String uuid) {
        return service.getUserByUUID(uuid);
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
