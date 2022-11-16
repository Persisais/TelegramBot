package com.persisais.telegrambot.memory;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class ActionInfo {
    private int actionTypeId;
    private int tovarId;

    public ActionInfo(int actionTypeId, int tovarId) {
        this.actionTypeId=actionTypeId;
        this.tovarId=tovarId;
    }


}
