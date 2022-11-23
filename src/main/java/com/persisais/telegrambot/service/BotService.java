package com.persisais.telegrambot.service;

import com.persisais.telegrambot.model.*;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class BotService {

    public RestTemplate restTemplate = new RestTemplate();

    public String http = "http://localhost:8080/api/users";
    public String http2 = "http://localhost:8080/api/tovar/get";
    public String http3 = "http://localhost:8080/api/tovar/id/";
    public String http4 = "http://localhost:8080/api/tovar/get/category/";
    public String http5 = "http://localhost:8080/api/carts/";
    public String http6 = "http://localhost:8080/api/users/get/tg/";
    public String httpGetCart ="http://localhost:8080/api/carts/get/";
    public String httpGetCategories = "http://localhost:8080/api/category/get";
    public String httpGetRemind = "http://localhost:8080/api/remind/get/";
    public String httpPostRemind = "http://localhost:8080/api/remind/";
    public String httpGetPhoto = "http://localhost:8080/api/tovar/get/img/";
    public String httpBuy = "http://localhost:8080/api/tovar/get/img/";
    public String httpChangeUser = "http://localhost:8080/api/users/update";
    public String httpRemoveTovarFromCart = "http://localhost:8080/api/carts/remove/";



    HttpHeaders createHeaders(Long id_telegram){
        return new HttpHeaders() {{
            String username, password;
            if (id_telegram == 1675364273) {
                username = "Admin";
                password = "Admin";
                System.out.println("Админ замечен");
            }
            else {
                username = "User";
                password = "User";
                System.out.println("Юзер замечен");
            }
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(
                    auth.getBytes(Charset.forName("US-ASCII")));
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }



    public void addUser(Long id_telegram, String name, String firstname, String lastname,String phone,String mail, boolean agreement) {
        HttpHeaders headers = createHeaders(id_telegram);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> map = new HashMap<>();
        map.put("id_telegram", id_telegram);
//        map.put("id_chat", id_chat);
        map.put("name", name);
        map.put("firstname", firstname);
        map.put("lastname", lastname);
        map.put("phone", phone);
        map.put("mail", mail);
        map.put("agreement", agreement);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(http, entity, String.class);
    }

    public void changeUser(Long id_telegram, String name, String firstname, String lastname, boolean agreement) {
        HttpHeaders headers = createHeaders(id_telegram);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        Map<String, Object> map = new HashMap<>();
        map.put("id_telegram", id_telegram);
        map.put("name", name);
        map.put("firstname", firstname);
        map.put("lastname", lastname);
        map.put("agreement", agreement);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(new URI(httpChangeUser), HttpMethod.PUT, entity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void changeUser(Long id_telegram, String phone, boolean agreement) {
        HttpHeaders headers = createHeaders(id_telegram);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        Map<String, Object> map = new HashMap<>();
        map.put("id_telegram", id_telegram);
        map.put("phone", phone);
        map.put("agreement", agreement);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(new URI(httpChangeUser), HttpMethod.PUT, entity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void changeUser(String mail, Long id_telegram, boolean agreement) {
        HttpHeaders headers = createHeaders(id_telegram);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        Map<String, Object> map = new HashMap<>();
        map.put("id_telegram", id_telegram);
        map.put("mail", mail);
        map.put("agreement", agreement);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(new URI(httpChangeUser), HttpMethod.PUT, entity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void changeUser(Long id_telegram, boolean agreement) {
        HttpHeaders headers = createHeaders(id_telegram);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        Map<String, Object> map = new HashMap<>();
        map.put("id_telegram", id_telegram);
        map.put("agreement", agreement);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(new URI(httpChangeUser), HttpMethod.PUT, entity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToCart(Long id_telegram, int id_tovar, int quantity) {
        HttpHeaders headers = createHeaders(id_telegram);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        Map<String, Object> map= new HashMap<>();
        Map<String, Object> internalMap= new HashMap<>();
        internalMap.put("id",id_tovar);
        map.put("quantity", quantity);
        map.put("tovar", internalMap);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(http5+id_telegram, entity, String.class);
    }
    public void addToRemind(Long id_telegram, Long id_tovar, int quantity) {
        HttpHeaders headers = createHeaders(id_telegram);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        Map<String, Object> map= new HashMap<>();
        map.put("id",id_tovar);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(httpPostRemind+id_telegram+"?quantity="+quantity, entity, String.class);
    }

    public UsersDto getUserByTg(Long id_telegram) {
        HttpHeaders headers = createHeaders(id_telegram);
        HttpEntity request = new HttpEntity(headers);

        UsersDto response = null;
        try {
            response = restTemplate.exchange(new URI(http6+id_telegram), HttpMethod.GET, request, UsersDto.class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public TovarDto[] getTovar(Long id_telegram) {
        HttpHeaders headers = createHeaders(id_telegram);
        HttpEntity request = new HttpEntity(headers);
        TovarDto[] response = null;
                try {
            response = restTemplate.exchange(new URI(http2), HttpMethod.GET, request,  TovarDto[].class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public byte[] getTovarImage(Long id_telegram, Long id) {
        HttpHeaders headers = createHeaders(id_telegram);
        HttpEntity request = new HttpEntity(headers);
        byte[] response = null;
        try {
            response = restTemplate.exchange(new URI(httpGetPhoto+id), HttpMethod.GET, request,  byte[].class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public TovarDto[] getTovarByCategory(Long id_telegram, long id_category) {
        HttpHeaders headers = createHeaders(id_telegram);
        HttpEntity request = new HttpEntity(headers);
        TovarDto[] response = null;
        try {
            //TODO Выбор категории на кнопку
            response = restTemplate.exchange(new URI(http4+id_category), HttpMethod.GET, request, TovarDto[].class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public TovarDto getTovarById(int id, Long id_telegram) {
        HttpHeaders headers = createHeaders(id_telegram);
        HttpEntity request = new HttpEntity(headers);
        TovarDto response = null;
        try {
            response = restTemplate.exchange(new URI(http3+id), HttpMethod.GET, request, TovarDto.class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public TrashDto[] getCart(Long id_telegram) {
        HttpHeaders headers = createHeaders(id_telegram);
        HttpEntity request = new HttpEntity(headers);
        TrashDto[] response = null;
        try {
            response = restTemplate.exchange(new URI(httpGetCart+id_telegram), HttpMethod.GET, request, TrashDto[].class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public RemindDto[] getRemind(Long id_telegram) {
        HttpHeaders headers = createHeaders(id_telegram);
        HttpEntity request = new HttpEntity(headers);
        RemindDto[] response = null;
        try {
            response = restTemplate.exchange(new URI(httpGetRemind+id_telegram), HttpMethod.GET, request, RemindDto[].class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public CategoryDto[] getCategories(Long id_telegram) {
        HttpHeaders headers = createHeaders(id_telegram);
        HttpEntity request = new HttpEntity(headers);
        CategoryDto[] response = null;
        try {
            response = restTemplate.exchange(new URI(httpGetCategories), HttpMethod.GET, request, CategoryDto[].class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


}
