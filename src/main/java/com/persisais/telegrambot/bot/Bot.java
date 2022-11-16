package com.persisais.telegrambot.bot;

import com.persisais.telegrambot.Service.BotService;
import com.persisais.telegrambot.memory.ActionInfo;
import com.persisais.telegrambot.model.CategoryDto;
import com.persisais.telegrambot.model.RemindDto;
import com.persisais.telegrambot.model.TovarDto;
import com.persisais.telegrambot.model.TrashDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {

    private HashMap<Long, ActionInfo> actions= new HashMap<Long, ActionInfo>();

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

    public final String emptyImage = "https://www.ponycorral.ca/wp-content/uploads/2015/07/placeholder-image-1000x1000-150x150.png";

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

    public void sendTovarInfo(Message message, TovarDto[] tovarArr) {
        int r, l;
        for (int i=0; i< (int)Math.ceil(tovarArr.length/5.0); i++) {
            String messageText="";
            l= 5*i;
            r= Math.min(l + 5, tovarArr.length);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> keyboardButtonsRow= new ArrayList<>();
            List<InlineKeyboardButton> keyboardButtonsSecondRow= new ArrayList<>();
            List<InputMedia> media = new ArrayList<>();
            for (int j=l; j<r; j++) {
                messageText+=tovarArr[j]+"\n----------------\n";
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

                //TODO Сделать sendPhoto, вместо sendMediaGroup, если в пятерке товаров только 1 товар

                InputMedia photo = new InputMediaPhoto();
                if (tovarArr[j].getPhoto()!=null) {
                    String pathname ="images/";
                    byte[] image = botService.getTovarImage(message.getFrom().getId(), tovarArr[j].getId());
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
            TovarDto[] tovarArr;
            int l, r;

            if (actions.get(message.getFrom().getId())!=null && message.getText().chars().allMatch( Character::isDigit )) {
                ActionInfo action = actions.get(message.getFrom().getId());
                try {
                    if (action.getActionTypeId()==0) {
                        botService.addToCart(message.getFrom().getId(),action.getTovarId(),Integer.parseInt(message.getText()));
                        sendMsg(message, "Добавил товар №"+action.getTovarId()+" в количесте "+message.getText()+" в корзину");
                        //TODO проверка на количество
                        actions.remove(message.getFrom().getId());
                    }
                    else if (action.getActionTypeId()==1) {
                        botService.addToRemind(message.getFrom().getId(), action.getTovarId(), Integer.parseInt(message.getText()));
                        sendMsg(message, "Добавил товар №" + action.getTovarId() + " в количесте " + message.getText() + " в избранное");
                        actions.remove(message.getFrom().getId());
                    }
                }
                catch (HttpServerErrorException e) {
                    //TODO посмотреть мб другую ошибку
                    sendMsg(message,"У нас нет столько товара на складе");
                }


            }
            else {
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
                        Long id_telegram = message.getFrom().getId();
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
                        tovarArr = botService.getTovar(message.getFrom().getId());
                        sendTovarInfo(message, tovarArr);
                        break;
                    case "/get_tovar_by_category":
                        CategoryDto[] categoriesArr = botService.getCategories(message.getFrom().getId());
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                        List<InlineKeyboardButton> keyboardButtonsRow;
                        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                        String messageText="*Категории:*\n";
                        for (int i=0; i< (int)Math.ceil(categoriesArr.length/5.0); i++) {
                            System.out.println(i);
                            l= 5*i;
                            r= Math.min(l + 5, categoriesArr.length);
                            keyboardButtonsRow= new ArrayList<>();
                            rowList = new ArrayList<>();
                            for (int j=l; j<r; j++) {
                                messageText+=categoriesArr[j]+"\n----------------\n";
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
                        CategoryDto[] categoryArr =botService.getCategories(message.getFrom().getId());
                        messageText = "*Категории:*";
                        for (CategoryDto category: categoryArr) {
                            messageText += category+"\n----------------\n";
                        }
                        sendMsg(message, messageText);
                        break;
                    case "/get_remind":
                        RemindDto[] remindArr = botService.getRemind(message.getFrom().getId());
                        for (RemindDto remind : remindArr) {
                            sendMsg(message, remind.toString());
                        }
                        break;
                    case "/get_cart":
                        TrashDto[] trashArr= botService.getCart(message.getFrom().getId());
                        messageText ="*Ваша корзина*:\n";
                        for (TrashDto trash : trashArr) {
                            messageText+=trash+"\n----------------\n";
                        }
                        sendMsg(message, messageText);
                        break;
                    case "/buy":
                        trashArr= botService.getCart(message.getFrom().getId());
                        messageText ="*Ваша корзина*:\n";
                        for (TrashDto trash : trashArr) {
                            messageText+=trash+"\n----------------\n";
                        }
                        inlineKeyboardMarkup = new InlineKeyboardMarkup();
                        keyboardButtonsRow = new ArrayList<>();
                        rowList = new ArrayList<>();
                        InlineKeyboardButton buttonYes = new InlineKeyboardButton();
                        InlineKeyboardButton buttonNo = new InlineKeyboardButton();
                        messageText+="\n[Ссылочка, которая не работает](http://localhost:8080/buy/1675364273)";
                        messageText+="\n`http://localhost:8080/buy/"+message.getFrom().getId()+"`";
                        //buttonYes.setUrl("http://localhost:8080/buy/"+message.getFrom().getId());
                        //это должна быть нормальная ссылка, которую может открыть телеграм апи
                        //1675364273

                        //buttonYes.setUrl("yandex.ru");
                        buttonYes.setText("Да");
                        buttonYes.setCallbackData("YES");
                        buttonNo.setText("Нет");
                        buttonNo.setCallbackData("NO");
                        keyboardButtonsRow.add(buttonYes);
                        keyboardButtonsRow.add(buttonNo);
                        rowList.add(keyboardButtonsRow);
                        inlineKeyboardMarkup.setKeyboard(rowList);
                        sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);


                        //sendMsg(message, messageText);
                        break;
                    default:
                        sendMsg(message, "Бип-буп, я робот-идиот, команда не распознана");
                        break;


                }
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
                Message message = new Message();
                message.setChat(callbackQuery.getMessage().getChat());
                message.setFrom(callbackQuery.getFrom());
                sendTovarInfo(message, tovarArr);
                answerCallbackQuery(callbackQuery, "Показываю товары в категории №"+Integer.parseInt(command.substring(9)));
            }

            else if (command.startsWith("fav")) {
                //botService.addToRemind(callbackQuery.getFrom().getId(),Integer.parseInt(command.substring(3)), 1);
                ActionInfo actionInfo = new ActionInfo(1,Integer.parseInt(command.substring(3)));
                actions.put(callbackQuery.getFrom().getId(),actionInfo);
                answerCallbackQuery(callbackQuery, "Напишите, сколько товара №"+command.substring(3)+" вы хотите добавить в любимое");

            }
            else {
                //botService.addToCart(callbackQuery.getFrom().getId(),Integer.parseInt(command),1);
                ActionInfo actionInfo = new ActionInfo(0,Integer.parseInt(command));
                actions.put(callbackQuery.getFrom().getId(),actionInfo);
                answerCallbackQuery(callbackQuery, "Напишите, сколько товара №"+command+" вы хотите добавить в корзину");

            }
        }
    }
}
