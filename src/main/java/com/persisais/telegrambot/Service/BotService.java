package com.persisais.telegrambot.Service;

import com.persisais.telegrambot.model.CategoryDataDto;
import com.persisais.telegrambot.model.CategoryDto;
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
    public String http6 = "http://localhost:8080/api/users/get/tg/";
    public String httpGetCart ="http://localhost:8080/api/carts/get/";
    public String httpGetCategories = "http://localhost:8080/api/category/get";
    public String httpGetRemind = "http://localhost:8080/api/remind/get/";
    public String httpPostRemind = "http://localhost:8080/api/remind/";



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
        HttpHeaders headers = null;
        headers = createHeaders(id_telegram);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> map = new HashMap<>();
        map.put("id_telegram", id_telegram);
        map.put("name", name);
        map.put("firstname", firstname);
        map.put("lastname", lastname);
        map.put("phone", phone);
        map.put("mail", mail);
        map.put("agreement", agreement);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(http, entity, String.class);
    }

    public void addToCart(Long id_telegram, int id_tovar, int quantity) {
        HttpHeaders headers = new HttpHeaders();
        headers = createHeaders(id_telegram);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        UsersDto user = getUserByTg(id_telegram);
        Long id_user = user.getId();
        //TovarDto tovar = getTovarById(id_tovar, id_telegram);
        Map<String, Object> map= new HashMap<>();
        Map<String, Object> internalMap= new HashMap<>();
        //ты уверен что это всё, что я должен в body отправить?
        internalMap.put("id",id_tovar);
        map.put("quantity", quantity);
        map.put("tovar", internalMap);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(http5+id_user, entity, String.class);
    }
    public void addToRemind(Long id_telegram, int id_tovar, int quantity) {
        HttpHeaders headers = new HttpHeaders();
        headers = createHeaders(id_telegram);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        UsersDto user = getUserByTg(id_telegram);
        Long id_user = user.getId();
        Map<String, Object> map= new HashMap<>();
        map.put("id",id_tovar);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(httpPostRemind+id_user+"?quantity="+quantity, entity, String.class);
    }

    public UsersDto getUserByTg(Long id_telegram) {
        HttpHeaders headers = new HttpHeaders();
        headers = createHeaders(id_telegram);
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
        HttpHeaders headers = new HttpHeaders();
        headers = createHeaders(id_telegram);
        HttpEntity request = new HttpEntity(headers);
        TovarDto[] response = null;
                try {
            response = restTemplate.exchange(new URI(http2), HttpMethod.GET, request,  TovarDto[].class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public TovarDto[] getTovarByCategory(Long id_telegram) {
        HttpHeaders headers = new HttpHeaders();
        headers = createHeaders(id_telegram);
        HttpEntity request = new HttpEntity(headers);
        TovarDto[] response = null;
        try {
            response = restTemplate.exchange(new URI(http4), HttpMethod.GET, request, TovarDto[].class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    public TovarDto getTovarById(int id, Long id_telegram) {
        HttpHeaders headers = new HttpHeaders();
        headers = createHeaders(id_telegram);
        HttpEntity request = new HttpEntity(headers);
        TovarDto response = null;
        try {
            response = restTemplate.exchange(new URI(http3+id), HttpMethod.GET, request, TovarDto.class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public TovarDto[] getCart(Long id_telegram) {
        HttpHeaders headers = new HttpHeaders();
        headers = createHeaders(id_telegram);
        UsersDto user = getUserByTg(id_telegram);
        Long id_user = user.getId();
        HttpEntity request = new HttpEntity(headers);
        TovarDto[] response = null;
        try {
            response = restTemplate.exchange(new URI(httpGetCart+id_user), HttpMethod.GET, request, TovarDto[].class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public TovarDto[] getRemind(Long id_telegram) {
        HttpHeaders headers = new HttpHeaders();
        headers = createHeaders(id_telegram);
        UsersDto user = getUserByTg(id_telegram);
        Long id_user = user.getId();
        HttpEntity request = new HttpEntity(headers);
        TovarDto[] response = null;
        try {
            response = restTemplate.exchange(new URI(httpGetRemind+id_user), HttpMethod.GET, request, TovarDto[].class).getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }



    public CategoryDto[] getCategories(Long id_telegram) {
        HttpHeaders headers = new HttpHeaders();
        headers = createHeaders(id_telegram);
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
