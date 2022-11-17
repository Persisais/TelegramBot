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
public class UsersDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("id_telegram")
    private Long id_telegram;

    @JsonProperty("name")
    private String name;

    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("lastname")
    private String lastname;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("mail")
    private String mail;

    @JsonProperty("agreement")
    private boolean agreement;

    public boolean getAgreement() {
        return this.agreement;
    }

    public String getCurrentAgreementSmile () {
        String noSmile = "❌";
        String yesSmile ="✅";
        String currentSmile = agreement ? yesSmile : noSmile;
        return currentSmile;
    }

    @Override
    public String toString () {



        return ("*Ник:* "+ name +
                "\n*Имя: *"+firstname +
                "\n*Фамилия: *" + lastname +
                "\n*Номер: *" + phone +
                "\n*Почта:* " + mail +
                "\n*Согласие: *" + getCurrentAgreementSmile());
    }

}
