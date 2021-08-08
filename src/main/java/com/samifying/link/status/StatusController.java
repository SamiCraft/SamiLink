package com.samifying.link.status;

import com.pequla.server.ping.StatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(path = "/api/status")
public class StatusController {

    private final StatusService service;

    @Autowired
    public StatusController(StatusService service) {
        this.service = service;
    }

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
