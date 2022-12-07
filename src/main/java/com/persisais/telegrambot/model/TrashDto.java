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
public class TrashDto {

    @JsonProperty("id корзины")
    private Long id;

    @JsonProperty("tovar")
    private TovarDto tovar;

    @JsonProperty("quantity")
    private double quantity;

    @JsonProperty("cart")
    private CartsDto cart;

    @Override
    public String toString () {
        return ("*Товар: *\n"+tovar +
                "\n*Количество: *" + quantity);
    }
    public String toStringMedia () {
        return ("Товар: \n"+tovar.toStringMedia() +
                "\nКоличество: " + quantity);
    }
}
