package com.samifying.link.data;

import com.samifying.link.error.ErrorObject;
import com.samifying.link.error.LoginRejectedException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping(path = "/api")
public class DataController {

    private final DataService service;

    @Autowired
    public DataController(DataService service) {
        this.service = service;
    }

    @GetMapping(path = "/user/{uuid}")
    public ResponseEntity<UserData> getUser(@PathVariable String uuid) {
        return service.getUserByUUID(uuid);
    }

    @GetMapping(path = "/data")
    public Page<Data> getAll(Pageable page) {
        return service.getAllData(page);
    }

    @GetMapping(path = "/data/{id}")
    public ResponseEntity<Data> getById(@PathVariable int id) {
        return service.getDataById(id);
    }

    @GetMapping(path = "/data/discord/{id}")
    public ResponseEntity<Data> getByDiscordId(@PathVariable String id) {
        return service.getDataByDiscordId(id);
    }

    @ExceptionHandler(LoginRejectedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorObject handleLoginRejectedException(LoginRejectedException e, @NotNull HttpServletRequest request) {
        return new ErrorObject(e, request.getServletPath());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorObject handleException(Exception e, @NotNull HttpServletRequest request) {
        return new ErrorObject(e, request.getServletPath());
    }
}
