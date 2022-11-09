package com.persisais.telegrambot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RemindDto {

    @JsonProperty("id корзины")
    private Long id;

    @JsonProperty("user")
    private UsersDto user;

    @JsonProperty("tovar")
    private TovarDto tovar;

    @JsonProperty("is_delivered")
    private boolean is_delivered;

    @JsonProperty("quantity")
    private int quantity;

    public String toString () {
        return ("*Товар в избранном: *\n"+tovar +
                "\n*Количество: *" + quantity);
    }
}
