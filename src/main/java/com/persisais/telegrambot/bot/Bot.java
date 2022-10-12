package com.persisais.telegrambot.bot;

import com.persisais.telegrambot.Service.BotService;
import com.persisais.telegrambot.model.CategoryDataDto;
import com.persisais.telegrambot.model.TovarDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.security.auth.callback.Callback;
import java.util.ArrayList;
import java.util.List;

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

    public void sendInlineKeyboardMsg(Message message, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void answerCallbackQuery(CallbackQuery callbackQuery, String text) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQuery.getId());
        answer.setText(text);
        answer.setShowAlert(true);
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }





    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            TovarDto[] tovarArr = null;
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
                    Long id_telegram = Long.valueOf(message.getFrom().getId());
                    String name = message.getChat().getUserName();
                    String firstname = message.getChat().getFirstName();
                    String lastname = message.getChat().getLastName();
                    String phone = "88005553535";
                    String mail = "1@gmail.com";
                    boolean agreement = true;
                    botService.addUser(id_telegram, name, firstname, lastname, phone, mail, agreement);
                    sendMsg(message, "Я тебя запомнил");
                    break;
                case "/get_tovar":
                    tovarArr = botService.getTovar();
                    int l, r;
                    for (int i=0; i< (int)Math.ceil(tovarArr.length/5.0); i++) {
                        System.out.println(i);
                        String messageText="";
                        l= 5*i;
                        r= Math.min(l + 5, tovarArr.length);
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                        //ArrayList<InlineKeyboardButton> buttonsArray = new ArrayList<>();
                        List<InlineKeyboardButton> keyboardButtonsRow= new ArrayList<>();
                        for (int j=l; j<r; j++) {
                            messageText+=tovarArr[j].toString()+"\n----------------\n";
                            InlineKeyboardButton button = new InlineKeyboardButton();
                            button.setText(tovarArr[j].getId().toString());
                            button.setCallbackData(tovarArr[j].getId().toString());
                            keyboardButtonsRow.add(button);
                        }
                        InlineKeyboardButton button = new InlineKeyboardButton();
                        button.setText("-->");
                        button.setCallbackData("next");
                        keyboardButtonsRow.add(button); //
                        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                        rowList.add(keyboardButtonsRow);
                        inlineKeyboardMarkup.setKeyboard(rowList);
                        System.out.println(messageText);
                        sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
                        //sendMsg(message, messageText);
                    }
                    /*
                    for (TovarDto tovar : tovarArr) {
                        sendMsg(message, tovar.toString());
                    }
                     */


                    break;
                case "/get_tovar_by_cat":
                    tovarArr = botService.getTovarByCategory();
                    for (TovarDto tovar : tovarArr) {
                        sendMsg(message, tovar.toString());
                    }
                    break;
                /*
                case "/get_categories":
                    CategoryDataDto categoryArr =botService.getCategories();
                    for (CategoryDto category: categoryArr.getData()) {
                        sendMsg(message, category.getName()+"\n"+category.getDescription());
                    }
                    break;
                 */
                default:
                    sendMsg(message, "Бип-буп, я робот-идиот, команда не распознана");
                    break;
            }
        }
        else if (update.hasCallbackQuery()) {
            String command = update.getCallbackQuery().getData();
            CallbackQuery callbackQuery = update.getCallbackQuery();
            if (command.equals("next")) {
                answerCallbackQuery(callbackQuery, "Показываю следующие товары (нет)");
            }
            else {
                answerCallbackQuery(callbackQuery, "Добавил товар "+command+" в корзину");
            }
        }
    }
}
