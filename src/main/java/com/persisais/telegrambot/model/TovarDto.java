package com.persisais.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.nio.charset.StandardCharsets;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TovarDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("category")
    private CategoryDto category;

    @JsonProperty("name")
    private String name;

    @JsonProperty("cost")
    private double cost;

    @JsonProperty("quantity_in_stock")
    private double quantity_in_stock;

    @JsonProperty("description")
    private String description;

    @JsonProperty("photo")
    private byte[] photo;

    @Override
    public String toString () {
       return ("*Номер товара:* "+ id+"" +
               "\n*Название товара: *"+name +
               "\n*Категория: *" + category.getName() +
               "\n*Количество на складе: *" + quantity_in_stock +
               "\n*Цена: *" + cost +
               "_ ₽_\n*Описание:* " + description);
    }
    public String toStringMedia () {
        return ("Номер товара: "+ id+"" +
                "\nНазвание товара: "+name +
                "\nКатегория: " + category.getName() +
                "\nКоличество на складе: " + quantity_in_stock +
                "\nЦена: " + cost +
                " ₽\nОписание: " + description);
    }

}
