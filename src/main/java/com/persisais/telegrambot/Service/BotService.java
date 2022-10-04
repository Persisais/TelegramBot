package com.persisais.telegrambot.Service;

import com.persisais.telegrambot.model.TovarDataDto;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class BotService {

    public RestTemplate restTemplate = new RestTemplate();
    public String http = "http://localhost:8080/api/users";
    public String http2 = "http://localhost:8080/api/tovar";

    public void addUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> map= new HashMap<>();
        map.put("agreement", "true");
        map.put("mail", "sdsdfds");
        map.put("name", "ass2");
        map.put("phone", "88005553535");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(http, entity, String.class);
    }

    public TovarDataDto getTovar() {
        TovarDataDto response = null;
                try {
            response = restTemplate.getForObject(new URI(http2), TovarDataDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;

    }


}
