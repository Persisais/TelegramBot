package com.persisais.telegrambot.bot;

import com.persisais.telegrambot.Service.BotService;
import com.persisais.telegrambot.Service.ImageConverter;
import com.persisais.telegrambot.model.*;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.security.auth.callback.Callback;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private BotService botService;
    public ImageConverter imageConverter = new ImageConverter();

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

    public void sendMediaGroup(Message message, List<InputMedia> media) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setChatId(message.getChatId().toString());
        sendMediaGroup.setReplyToMessageId(message.getMessageId());
        sendMediaGroup.setMedias(media);
        try {
            execute(sendMediaGroup);
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
            int l, r;
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
                    String emptyImage = "https://www.ponycorral.ca/wp-content/uploads/2015/07/placeholder-image-1000x1000-150x150.png";
                    tovarArr = botService.getTovar(Long.valueOf(message.getFrom().getId()));
                    l=0; r=0;
                    for (int i=0; i< (int)Math.ceil(tovarArr.length/5.0); i++) {
                        System.out.println(i);
                        String messageText="";
                        l= 5*i;
                        r= Math.min(l + 5, tovarArr.length);
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                        //ArrayList<InlineKeyboardButton> buttonsArray = new ArrayList<>();
                        List<InlineKeyboardButton> keyboardButtonsRow= new ArrayList<>();
                        List<InlineKeyboardButton> keyboardButtonsSecondRow= new ArrayList<>();
                        List<InputMedia> media = new ArrayList<>();
                        for (int j=l; j<r; j++) {
                            messageText+=tovarArr[j].toString()+"\n----------------\n";
                            InlineKeyboardButton button = new InlineKeyboardButton();
                            InlineKeyboardButton buttonSecond = new InlineKeyboardButton();
                            if (tovarArr[j].getQuantity_in_stock()>0) {
                                button.setText(tovarArr[j].getId().toString());
                                button.setCallbackData(tovarArr[j].getId().toString());
                                keyboardButtonsRow.add(button);
                            }
                            buttonSecond.setText("❤️"+tovarArr[j].getId().toString());
                            buttonSecond.setCallbackData("fav"+tovarArr[j].getId().toString());
                            keyboardButtonsSecondRow.add(buttonSecond);

                            InputMedia photo = new InputMediaPhoto();
                            if (tovarArr[j].getPhoto()!=null) {
                                String pathname ="images/";
                                byte[] image = botService.getTovarImage(Long.valueOf(message.getFrom().getId()), tovarArr[j].getId());
                                try {
                                    Files.write(Paths.get(pathname+tovarArr[j].getId()+".png"), image);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                photo.setMedia(new File(pathname+tovarArr[j].getId()+".png"), tovarArr[j].getId().toString());
//                                try {
//                                    photo.setMedia( new FileInputStream("images/"+tovarArr[j].getId()+".png"), tovarArr[j].getId().toString());
//                                } catch (FileNotFoundException e) {
//                                    throw new RuntimeException(e);
//                                }
                            }
                            else {
                                photo.setMedia(emptyImage);
                            }
                            photo.setMediaName(tovarArr[j].getId().toString());
                            photo.setCaption(tovarArr[j].toString());
                            media.add(photo);
                        }
                        if (r!=tovarArr.length) {
                            InlineKeyboardButton button = new InlineKeyboardButton();
                            button.setText("-->");
                            button.setCallbackData("next");
                            keyboardButtonsRow.add(button);
                        }
                        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                        rowList.add(keyboardButtonsRow);
                        rowList.add(keyboardButtonsSecondRow);
                        inlineKeyboardMarkup.setKeyboard(rowList);
                        sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
                        sendMediaGroup(message, media);
                    }

                    break;
                case "/get_tovar_by_category":
                    //TODO Объединить с get_categories
                    CategoryDto[] categoriesArr = botService.getCategories(Long.valueOf(message.getFrom().getId()));
                    l=0; r=0;
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    List<InlineKeyboardButton> keyboardButtonsRow= new ArrayList<>();
                    List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                    String messageText="";
                    for (int i=0; i< (int)Math.ceil(categoriesArr.length/5.0); i++) {
                        System.out.println(i);
                        messageText="";
                        l= 5*i;
                        r= Math.min(l + 5, categoriesArr.length);
                        keyboardButtonsRow= new ArrayList<>();
                        rowList = new ArrayList<>();
                        for (int j=l; j<r; j++) {
                            messageText+=categoriesArr[j].toString()+"\n----------------\n";
                            InlineKeyboardButton button = new InlineKeyboardButton();
                            button.setText(categoriesArr[j].getName());
                            button.setCallbackData("Category:"+categoriesArr[j].getId().toString());
                            keyboardButtonsRow.add(button);
                            }
                            rowList.add(keyboardButtonsRow);
                        }
                        inlineKeyboardMarkup.setKeyboard(rowList);
                        sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
                    break;

                case "/get_categories":
                    //TODO Объединить с get_tovar_by_catq
                    CategoryDto[] categoryArr =botService.getCategories(Long.valueOf(message.getFrom().getId()));
                    messageText = "*Категории:*\n";
                    for (CategoryDto category: categoryArr) {
                        messageText += category.getName()+"\n"+category.getDescription()+"\n----------------\n";
                    }
                    sendMsg(message, messageText);
                    break;
                case "/get_remind":
                    RemindDto[] remindArr = botService.getRemind(Long.valueOf(message.getFrom().getId()));
                    for (RemindDto remind : remindArr) {
                        sendMsg(message, remind.toString());
                    }
                    break;
                case "/get_cart":
                    TrashDto[] trashArr= botService.getCart(Long.valueOf(message.getFrom().getId()));
                    for (TrashDto trash : trashArr) {
                        sendMsg(message, trash.toString());
                    }
                    break;
                default:
                    sendMsg(message, "Бип-буп, я робот-идиот, команда не распознана");
                    break;
            }
        }
        else if (update.hasCallbackQuery()) {
            String command = update.getCallbackQuery().getData();
            CallbackQuery callbackQuery = update.getCallbackQuery();
            if (command.equals("next")) {
                //TODO
                answerCallbackQuery(callbackQuery, "Показываю следующие товары (нет)");
            }
            else if (command.startsWith("Category:")) {
                TovarDto[] tovarArr = botService.getTovarByCategory(callbackQuery.getFrom().getId(), Integer.parseInt(command.substring(9)));
                answerCallbackQuery(callbackQuery, "Показываю товары в категории №"+Integer.parseInt(command.substring(9)));
                String emptyImage = "https://www.ponycorral.ca/wp-content/uploads/2015/07/placeholder-image-1000x1000-150x150.png";
                int l=0; int r=0;
                for (int i=0; i< (int)Math.ceil(tovarArr.length/5.0); i++) {
                    System.out.println(i);
                    String messageText="";
                    l= 5*i;
                    r= Math.min(l + 5, tovarArr.length);
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    List<InlineKeyboardButton> keyboardButtonsRow= new ArrayList<>();
                    List<InlineKeyboardButton> keyboardButtonsSecondRow= new ArrayList<>();
                    List<InputMedia> media = new ArrayList<>();
                    for (int j=l; j<r; j++) {
                        messageText+=tovarArr[j].toString()+"\n----------------\n";
                        InlineKeyboardButton button = new InlineKeyboardButton();
                        InlineKeyboardButton buttonSecond = new InlineKeyboardButton();
                        if (tovarArr[j].getQuantity_in_stock()>0) {
                            button.setText(tovarArr[j].getId().toString());
                            button.setCallbackData(tovarArr[j].getId().toString());
                            keyboardButtonsRow.add(button);
                        }
                        buttonSecond.setText("❤️"+tovarArr[j].getId().toString());
                        buttonSecond.setCallbackData("fav"+tovarArr[j].getId().toString());
                        keyboardButtonsSecondRow.add(buttonSecond);

                        InputMedia photo = new InputMediaPhoto();
                        if (tovarArr[j].getPhoto()!=null) {
                            String pathname ="images/";
                            byte[] image = botService.getTovarImage(Long.valueOf(callbackQuery.getFrom().getId()), tovarArr[j].getId());
                            try {
                                Files.write(Paths.get(pathname+tovarArr[j].getId()+".png"), image);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            photo.setMedia(new File(pathname+tovarArr[j].getId()+".png"), tovarArr[j].getId().toString());
//                                try {
//                                    photo.setMedia( new FileInputStream("images/"+tovarArr[j].getId()+".png"), tovarArr[j].getId().toString());
//                                } catch (FileNotFoundException e) {
//                                    throw new RuntimeException(e);
//                                }
                        }
                        else {
                            photo.setMedia(emptyImage);
                        }
                        photo.setMediaName(tovarArr[j].getId().toString());
                        photo.setCaption(tovarArr[j].toString());
                        media.add(photo);
                    }
                    if (r!=tovarArr.length) {
                        InlineKeyboardButton button = new InlineKeyboardButton();
                        button.setText("-->");
                        button.setCallbackData("next");
                        keyboardButtonsRow.add(button);
                    }
                    List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                    rowList.add(keyboardButtonsRow);
                    rowList.add(keyboardButtonsSecondRow);
                    inlineKeyboardMarkup.setKeyboard(rowList);
                    Message message = new Message();
                    message.setChat(callbackQuery.getMessage().getChat());
                    sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
                    sendMediaGroup(message, media);
                }
            }

            else if (command.startsWith("fav")) {
                botService.addToRemind(Long.valueOf(callbackQuery.getFrom().getId()),Integer.parseInt(command.substring(3)), 1);
                answerCallbackQuery(callbackQuery, "Добавил товар "+command.substring(3)+" в любимое");

            }
            else {
                botService.addToCart(Long.valueOf(callbackQuery.getFrom().getId()),Integer.parseInt(command),1);
                answerCallbackQuery(callbackQuery, "Добавил товар "+command+" в корзину");

            }
        }
    }
}
