package com.samifying.link.controller;

import com.pequla.server.ping.StatusResponse;
import com.samifying.link.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(path = "/api/status")
public class StatusController {

    private final StatusService service;

    @GetMapping
    public ResponseEntity<StatusResponse> generateServerStatus() {
        return service.getDefaultServerStatus();
    }

    @GetMapping("/{hostname}")
    public ResponseEntity<StatusResponse> generateServerStatus(@PathVariable String hostname) {
        return service.getServerStatus(hostname);
    }

    @GetMapping("/{hostname}/{port}")
    public ResponseEntity<StatusResponse> generateServerStatus(@PathVariable String hostname, @PathVariable int port) {
        return service.getServerStatus(hostname, port);
    }
}
