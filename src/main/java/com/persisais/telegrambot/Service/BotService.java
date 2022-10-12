package com.persisais.telegrambot.Service;

import com.persisais.telegrambot.model.TovarDto;
import com.persisais.telegrambot.model.UsersDto;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;


import static com.sun.scenario.Settings.set;

@Component
public class BotService {

    public RestTemplate restTemplate = new RestTemplate();
    public String http = "http://localhost:8080/api/users";
    public String http2 = "http://localhost:8080/api/tovar/get";
    public String http3 = "http://localhost:8080/api/tovar/id/";
    public String http4 = "http://localhost:8080/api/tovar/get/category";
    public String http5 = "http://localhost:8080/api/carts/";
    public String http6 = "http://localhost:8080/api/users/tg/";
    //можем пока хотя бы админа прикрутить

    HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(
                    auth.getBytes(Charset.forName("US-ASCII")));
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }
    
    public void addUser(Long id_telegram, String name, String firstname, String lastname,String phone,String mail, boolean agreement) {
        HttpHeaders headers = null;
        if (id_telegram == 1675364273) {
            headers = createHeaders("Admin", "Admin");
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        }
        //тебе надо удалить моего пользователя в бд, там условие уникальности на тг айди. Я добавить не смогу

        Map<String, Object> map = new HashMap<>();
        map.put("id_telegram", id_telegram);
        map.put("name", firstname);
        map.put("firstname", name);
        map.put("lastname", lastname);
        map.put("phone", phone);
        map.put("mail", mail);
        map.put("agreement", agreement);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(http, entity, String.class);
    }

    public void addToCart(Long id_telegram, int id_tovar, int quantity) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        UsersDto user = getUserByTg(id_telegram);
        Long id_user = user.getId();
        TovarDto tovar = getTovarById(id_tovar);
        Map<String, Object> map= new HashMap<>();
        map.put("cart", map.put("user", user));
        map.put("quantity", quantity);
        map.put("tovar",tovar);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(http5+id_user, entity, String.class);
    }

    public UsersDto getUserByTg(Long id_telegram) {
        UsersDto response = null;
        try {
            response = restTemplate.getForObject(new URI(http6+id_telegram), UsersDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public TovarDto[] getTovar() {
        TovarDto[] response = null;
                try {
            response = restTemplate.getForObject(new URI(http2), TovarDto[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public TovarDto[] getTovarByCategory() {
        TovarDto[] response = null;
        try {
            response = restTemplate.getForObject(new URI(http4), TovarDto[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    public TovarDto getTovarById(int id) {
        TovarDto response = null;
        try {
            response = restTemplate.getForObject(new URI(http3+id), TovarDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /*
    public CategoryDataDto getCategories() {
        CategoryDataDto response = null;
        try {
            response = restTemplate.getForObject(new URI(http3), CategoryDataDto.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
     */



}
