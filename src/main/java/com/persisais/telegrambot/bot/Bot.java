package com.persisais.telegrambot.bot;

import com.persisais.telegrambot.Service.BotService;
import com.persisais.telegrambot.model.CategoryDataDto;
import com.persisais.telegrambot.model.CategoryDto;
import com.persisais.telegrambot.model.TovarDataDto;
import com.persisais.telegrambot.model.TovarDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private BotService botService;

    @Override
    public String getBotUsername() {
        return "FMv0.000003";
    }

    @Override
    public String getBotToken() {
        return "5463822852:AAF1k_KHQyWy2P9CKv0R6Z-X298TRb-XRSI";
    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            switch (message.getText()) {
                case "/start":
                    sendMsg(message, "Здравствуйте, " + message.getFrom().getFirstName() + "! Чтобы посмотреть список команд, введите /help");
                    break;
                case "/help":
                    sendMsg(message, "Никто тебе не поможет\nБот создан для интернет-магазина\nДоступные команды:\n" +
                            "/start - начать работу\n/help - помощь\n/setting - настройки\n/random - получить случайное число от 1 до 100");
                    break;
                case "/setting":
                    sendMsg(message, "И что ты хочешь настроить?");
                    break;
                case "/random":
                    sendMsg(message, String.valueOf((int) (Math.random() * 100 + 1)));
                    break;
                case "/add_user":
                    botService.addUser();
                    sendMsg(message, "Я тебя запомнил");
                    break;
                case "/get_tovar":
                    TovarDataDto tovarArr =botService.getTovar();
                    for (TovarDto tovar: tovarArr.getData()) {
                        sendMsg(message, tovar.getName()+"\n"+tovar.getCategory().getName()+"\nЦена:"+tovar.getCost()+"₽\n"+tovar.getDescription()+"\n"+tovar.getPhoto());
                    }
                    break;
                case "/get_categories":
                    CategoryDataDto categoryArr =botService.getCategories();
                    for (CategoryDto category: categoryArr.getData()) {
                        sendMsg(message, category.getName()+"\n"+category.getDescription());
                    }
                    break;
                default:
                    sendMsg(message, "Бип-буп, я робот-идиот, команда не распознана");
                    break;
            }
        }
    }

}
