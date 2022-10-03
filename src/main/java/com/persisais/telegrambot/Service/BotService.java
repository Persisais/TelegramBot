package com.persisais.telegrambot.Service;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class BotService {

    public RestTemplate restTemplate = new RestTemplate();
    public String http = "http://localhost:8080/api/users";

    public void addUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> map= new HashMap<>();
        map.put("agreement", "true");
        map.put("mail", "sdsdfds");
        map.put("name", "ass");
        map.put("phone", "100");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(http, entity, String.class);
    }
}
