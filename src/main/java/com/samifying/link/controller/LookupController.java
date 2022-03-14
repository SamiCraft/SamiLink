package com.samifying.link.controller;

import com.samifying.link.model.UserModel;
import com.samifying.link.service.LookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/lookup")
public class LookupController {

    private final LookupService service;

    @GetMapping(path = "/{username}")
    public UserModel lookupUser(@PathVariable String username) {
        return service.findUserByMinecraftUsername(username);
    }
}
