package com.samifying.link.controller;

import com.samifying.link.model.ProxyModel;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@CrossOrigin
@RequestMapping(path = "/api/proxy")
public class ProxyController {

    private final RestTemplate template;

    public ProxyController(RestTemplateBuilder builder) {
        this.template = builder.build();
    }

    @PostMapping
    public Object proxyOver(@RequestBody ProxyModel model) {
        String url = model.getUrl().toLowerCase();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        System.out.println(url);
        return template.getForObject(url, Object.class);
    }
}
