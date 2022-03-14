package com.samifying.link.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.samifying.link.error.MojangException;
import com.samifying.link.model.AccountModel;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class MojangService {

    private final HttpClient client;
    private final ObjectMapper mapper;

    public MojangService() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        // Register json mapper
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public AccountModel getAccountByUsername(String username) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + username))
                .header("Content-Type", "application/json")
                .GET().build();
        HttpResponse<String> rsp = client.send(request, HttpResponse.BodyHandlers.ofString());
        validateStatusCode(rsp);
        return mapper.readValue(rsp.body(), AccountModel.class);
    }

    public AccountModel getAccountByUUID(String uuid) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid))
                .header("Content-Type", "application/json")
                .GET().build();
        HttpResponse<String> rsp = client.send(request, HttpResponse.BodyHandlers.ofString());
        validateStatusCode(rsp);
        return mapper.readValue(rsp.body(), AccountModel.class);
    }

    private void validateStatusCode(@NotNull HttpResponse<String> rsp) {
        if (rsp.statusCode() != 200) {
            throw new MojangException("Server returned status code: " + rsp.statusCode());
        }
    }
}
