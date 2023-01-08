package com.persisais.telegrambot.bot;

import com.persisais.telegrambot.memory.ActionInfo;
import com.persisais.telegrambot.model.*;
import com.persisais.telegrambot.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.validation.constraints.Null;
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
    @Value("${bot.username}")
    private String username;

    @Value("${bot.token}")
    private String token;

    private TovarDto[] fullTovarArr;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
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

    public void sendPhoto (Message message, InputFile media, TovarDto tovar) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(message.getChatId().toString());
        sendPhoto.setReplyToMessageId(message.getMessageId());
        sendPhoto.setPhoto(media);
        sendPhoto.setCaption(tovar.toStringMedia());
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendTovarInfo(Message message, TovarDto[] tovarArr) {
        int r, l;
        int ITEMS_PER_MESSAGE=5;
        for (int i=0; i< (int)Math.ceil(tovarArr.length/(float)ITEMS_PER_MESSAGE); i++) {
            String messageText="";
            l= ITEMS_PER_MESSAGE*i;
            r= Math.min(l + ITEMS_PER_MESSAGE, tovarArr.length);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> keyboardButtonsRow= new ArrayList<>();
            List<InlineKeyboardButton> keyboardButtonsSecondRow= new ArrayList<>();
            List<InputMedia> media = new ArrayList<>();
            if (l==tovarArr.length-1) {
                messageText += tovarArr[l] + "\n----------------\n";
                InlineKeyboardButton button = new InlineKeyboardButton();
                InlineKeyboardButton buttonSecond = new InlineKeyboardButton();
                button.setText(tovarArr[l].getId().toString());
                button.setCallbackData("Cart:" + tovarArr[l].getId());
                keyboardButtonsRow.add(button);
                buttonSecond.setText("❤️" + tovarArr[l].getId().toString());
                buttonSecond.setCallbackData("Remind:" + tovarArr[l].getId().toString());
                keyboardButtonsSecondRow.add(buttonSecond);
                InputFile photo = new InputFile();
                if (tovarArr[l].getPhoto() != null) {
                    String pathname = "images/";
                    byte[] image = botService.getTovarImage(message.getFrom().getId(), tovarArr[l].getId());
                    try {
                        Files.write(Paths.get(pathname + tovarArr[l].getId() + ".png"), image);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    photo.setMedia(new File(pathname + tovarArr[l].getId() + ".png"), tovarArr[l].getId().toString());
                } else {
                    photo.setMedia(emptyImage);
                }
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                rowList.add(keyboardButtonsRow);
                rowList.add(keyboardButtonsSecondRow);
                inlineKeyboardMarkup.setKeyboard(rowList);
                sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
                sendPhoto(message, photo, tovarArr[l]);
            }
            else {
                for (int j=l; j<r; j++) {
                    messageText+=tovarArr[j]+"\n----------------\n";
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    InlineKeyboardButton buttonSecond = new InlineKeyboardButton();
                    if (tovarArr[j].getQuantity_in_stock()>0) {
                        button.setText(tovarArr[j].getId().toString());
                        button.setCallbackData("Cart:"+tovarArr[j].getId());
                        keyboardButtonsRow.add(button);
                    }
                    buttonSecond.setText("❤️"+tovarArr[j].getId().toString());
                    buttonSecond.setCallbackData("Remind:"+tovarArr[j].getId().toString());
                    keyboardButtonsSecondRow.add(buttonSecond);

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
                    photo.setCaption(tovarArr[j].toStringMedia());
                    media.add(photo);
                }

                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                rowList.add(keyboardButtonsRow);
                rowList.add(keyboardButtonsSecondRow);
                inlineKeyboardMarkup.setKeyboard(rowList);
                sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
                sendMediaGroup(message, media);
            }
        }
    }

    public void sendTovarInfo(Message message, TovarDto[] tovarArr, int iteration) {
        int r, l;
        int ITEMS_PER_MESSAGE=5;
        String messageText="";
        l= ITEMS_PER_MESSAGE*iteration;
        r= Math.min(l + ITEMS_PER_MESSAGE, tovarArr.length);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow= new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsSecondRow= new ArrayList<>();
        List<InputMedia> media = new ArrayList<>();
        if (l==tovarArr.length-1) {
            messageText += tovarArr[l] + "\n----------------\n";
            InlineKeyboardButton button = new InlineKeyboardButton();
            InlineKeyboardButton buttonSecond = new InlineKeyboardButton();
            button.setText(tovarArr[l].getId().toString());
            button.setCallbackData("Cart:" + tovarArr[l].getId());
            keyboardButtonsRow.add(button);
            buttonSecond.setText("❤️" + tovarArr[l].getId().toString());
            buttonSecond.setCallbackData("Remind:" + tovarArr[l].getId().toString());
            keyboardButtonsSecondRow.add(buttonSecond);
            InputFile photo = new InputFile();
            if (tovarArr[l].getPhoto() != null) {
                String pathname = "images/";
                byte[] image = botService.getTovarImage(message.getFrom().getId(), tovarArr[l].getId());
                try {
                    Files.write(Paths.get(pathname + tovarArr[l].getId() + ".png"), image);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                photo.setMedia(new File(pathname + tovarArr[l].getId() + ".png"), tovarArr[l].getId().toString());
            } else {
                photo.setMedia(emptyImage);
            }
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(keyboardButtonsRow);
            rowList.add(keyboardButtonsSecondRow);
            inlineKeyboardMarkup.setKeyboard(rowList);
            sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
            sendPhoto(message, photo, tovarArr[l]);
        }
        else {
            for (int j=l; j<r; j++) {
                messageText+=tovarArr[j]+"\n----------------\n";
                InlineKeyboardButton button = new InlineKeyboardButton();
                InlineKeyboardButton buttonSecond = new InlineKeyboardButton();
                if (tovarArr[j].getQuantity_in_stock()>0) {
                    button.setText(tovarArr[j].getId().toString());
                    button.setCallbackData("Cart:"+tovarArr[j].getId());
                    keyboardButtonsRow.add(button);
                }
                buttonSecond.setText("❤️"+tovarArr[j].getId().toString());
                buttonSecond.setCallbackData("Remind:"+tovarArr[j].getId().toString());
                keyboardButtonsSecondRow.add(buttonSecond);

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
                photo.setCaption(tovarArr[j].toStringMedia());
                media.add(photo);
            }
            if (r!=tovarArr.length) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("-->");
                button.setCallbackData("Next:"+(iteration+1));
                keyboardButtonsRow.add(button);
            }
            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(keyboardButtonsRow);
            rowList.add(keyboardButtonsSecondRow);
            inlineKeyboardMarkup.setKeyboard(rowList);
            sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
            sendMediaGroup(message, media);
        }
        for (int i=0; i< (int)Math.ceil(tovarArr.length/(float)ITEMS_PER_MESSAGE); i++) {

        }
    }

    public void sendCartInfo(Message message, TrashDto[] trashArr) {
        int r, l;
        int ITEMS_PER_MESSAGE=7;
        for (int i=0; i< (int)Math.ceil(trashArr.length/(float)ITEMS_PER_MESSAGE); i++){
            String messageText="";
            l= ITEMS_PER_MESSAGE*i;
            r= Math.min(l + ITEMS_PER_MESSAGE, trashArr.length);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> keyboardButtonsRow= new ArrayList<>();
            List<InlineKeyboardButton> keyboardButtonsSecondRow= new ArrayList<>();
            List<InputMedia> media = new ArrayList<>();
            //если в пачке товаров 1 товар. Мне это нужно чтобы отправлять инфу когда 1 товар в пачке

            if (l==trashArr.length-1) {
                messageText += trashArr[l] + "\n----------------\n";
//                InlineKeyboardButton button = new InlineKeyboardButton();
                InlineKeyboardButton buttonSecond = new InlineKeyboardButton();
//                button.setText("⚙️"+trashArr[l].getTovar().getId().toString());
//                button.setCallbackData("ChangeCart:" + trashArr[l].getTovar().getId());
//                keyboardButtonsRow.add(button);
                buttonSecond.setText("❌️" + trashArr[l].getTovar().getId().toString());
                buttonSecond.setCallbackData("DeleteCart:" + trashArr[l].getTovar().getId().toString());
                keyboardButtonsSecondRow.add(buttonSecond);
                InputFile photo = new InputFile();
                if (trashArr[l].getTovar().getPhoto() != null) {
                    String pathname = "images/";
                    byte[] image = botService.getTovarImage(message.getFrom().getId(), trashArr[l].getTovar().getId());
                    try {
                        Files.write(Paths.get(pathname + trashArr[l].getTovar().getId() + ".png"), image);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    photo.setMedia(new File(pathname + trashArr[l].getTovar().getId() + ".png"), trashArr[l].getTovar().getId().toString());
                } else {
                    photo.setMedia(emptyImage);
                }
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
//                rowList.add(keyboardButtonsRow);
                rowList.add(keyboardButtonsSecondRow);
                inlineKeyboardMarkup.setKeyboard(rowList);
                sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
                sendPhoto(message, photo, trashArr[l].getTovar());
            }
            else {
                for (int j=l; j<r; j++) {
                    messageText+=trashArr[j]+"\n----------------\n";
//                    InlineKeyboardButton button = new InlineKeyboardButton();
                    InlineKeyboardButton buttonSecond = new InlineKeyboardButton();
//                    button.setText("⚙️"+trashArr[j].getTovar().getId().toString());
//                    button.setCallbackData("ChangeCart:" + trashArr[j].getTovar().getId());
//                    keyboardButtonsRow.add(button);
                    buttonSecond.setText("❌️" + trashArr[j].getTovar().getId().toString());
                    buttonSecond.setCallbackData("DeleteCart:" + trashArr[j].getTovar().getId().toString());
                    keyboardButtonsSecondRow.add(buttonSecond);

                    InputMedia photo = new InputMediaPhoto();
                    if (trashArr[j].getTovar().getPhoto()!=null) {
                        String pathname ="images/";
                        byte[] image = botService.getTovarImage(message.getFrom().getId(), trashArr[j].getTovar().getId());
                        try {
                            Files.write(Paths.get(pathname+trashArr[j].getTovar().getId()+".png"), image);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        photo.setMedia(new File(pathname+trashArr[j].getTovar().getId()+".png"), trashArr[j].getTovar().getId().toString());
//                                try {
//                                    photo.setMedia( new FileInputStream("images/"+tovarArr[j].getId()+".png"), tovarArr[j].getId().toString());
//                                } catch (FileNotFoundException e) {
//                                    throw new RuntimeException(e);
//                                }
                    }
                    else {
                        photo.setMedia(emptyImage);
                    }
                    photo.setMediaName(trashArr[j].getTovar().getId().toString());
                    photo.setCaption(trashArr[j].toStringMedia());
                    media.add(photo);
                }
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
//                rowList.add(keyboardButtonsRow);
                rowList.add(keyboardButtonsSecondRow);
                inlineKeyboardMarkup.setKeyboard(rowList);
                sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
                sendMediaGroup(message, media);
            }
        }
    }

    public void sendRemindInfo(Message message, RemindDto[] remindArr) {
        int r, l;
        int ITEMS_PER_MESSAGE=7;
        for (int i=0; i< (int)Math.ceil(remindArr.length/(float)ITEMS_PER_MESSAGE); i++){
            String messageText="";
            l= ITEMS_PER_MESSAGE*i;
            r= Math.min(l + ITEMS_PER_MESSAGE, remindArr.length);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> keyboardButtonsRow= new ArrayList<>();
            List<InlineKeyboardButton> keyboardButtonsSecondRow= new ArrayList<>();
            List<InputMedia> media = new ArrayList<>();
            //если в пачке товаров 1 товар. Мне это нужно чтобы отправлять инфу когда 1 товар в пачке

            if (l==remindArr.length-1) {
                messageText += remindArr[l] + "\n----------------\n";
//                InlineKeyboardButton button = new InlineKeyboardButton();
                InlineKeyboardButton buttonSecond = new InlineKeyboardButton();
//                button.setText("⚙️"+trashArr[l].getTovar().getId().toString());
//                button.setCallbackData("ChangeCart:" + trashArr[l].getTovar().getId());
//                keyboardButtonsRow.add(button);
                buttonSecond.setText("❌️" + remindArr[l].getTovar().getId().toString());
                buttonSecond.setCallbackData("DeleteRemind:" + remindArr[l].getTovar().getId().toString());
                keyboardButtonsSecondRow.add(buttonSecond);
                InputFile photo = new InputFile();
                if (remindArr[l].getTovar().getPhoto() != null) {
                    String pathname = "images/";
                    byte[] image = botService.getTovarImage(message.getFrom().getId(), remindArr[l].getTovar().getId());
                    try {
                        Files.write(Paths.get(pathname + remindArr[l].getTovar().getId() + ".png"), image);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    photo.setMedia(new File(pathname + remindArr[l].getTovar().getId() + ".png"), remindArr[l].getTovar().getId().toString());
                } else {
                    photo.setMedia(emptyImage);
                }
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
//                rowList.add(keyboardButtonsRow);
                rowList.add(keyboardButtonsSecondRow);
                inlineKeyboardMarkup.setKeyboard(rowList);
                sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
                sendPhoto(message, photo, remindArr[l].getTovar());
            }
            else {
                for (int j=l; j<r; j++) {
                    messageText+=remindArr[j]+"\n----------------\n";
//                    InlineKeyboardButton button = new InlineKeyboardButton();
                    InlineKeyboardButton buttonSecond = new InlineKeyboardButton();
//                    button.setText("⚙️"+trashArr[j].getTovar().getId().toString());
//                    button.setCallbackData("ChangeCart:" + trashArr[j].getTovar().getId());
//                    keyboardButtonsRow.add(button);
                    buttonSecond.setText("❌️" + remindArr[j].getTovar().getId().toString());
                    buttonSecond.setCallbackData("DeleteRemind:" + remindArr[j].getTovar().getId().toString());
                    keyboardButtonsSecondRow.add(buttonSecond);

                    InputMedia photo = new InputMediaPhoto();
                    if (remindArr[j].getTovar().getPhoto()!=null) {
                        String pathname ="images/";
                        byte[] image = botService.getTovarImage(message.getFrom().getId(), remindArr[j].getTovar().getId());
                        try {
                            Files.write(Paths.get(pathname+remindArr[j].getTovar().getId()+".png"), image);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        photo.setMedia(new File(pathname+remindArr[j].getTovar().getId()+".png"), remindArr[j].getTovar().getId().toString());
//                                try {
//                                    photo.setMedia( new FileInputStream("images/"+tovarArr[j].getId()+".png"), tovarArr[j].getId().toString());
//                                } catch (FileNotFoundException e) {
//                                    throw new RuntimeException(e);
//                                }
                    }
                    else {
                        photo.setMedia(emptyImage);
                    }
                    photo.setMediaName(remindArr[j].getTovar().getId().toString());
                    photo.setCaption(remindArr[j].toStringMedia());
                    media.add(photo);
                }
                List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
//                rowList.add(keyboardButtonsRow);
                rowList.add(keyboardButtonsSecondRow);
                inlineKeyboardMarkup.setKeyboard(rowList);
                sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
                sendMediaGroup(message, media);
            }
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
    public void sendUserInfo (Message message) {
        String messageText = botService.getUserByTg(message.getFrom().getId()).toString();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow= new ArrayList<>();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        InlineKeyboardButton buttonUpdateInfo = new InlineKeyboardButton();
        buttonUpdateInfo.setText("Обновить");
        buttonUpdateInfo.setCallbackData("UpdateUserInfo");
        keyboardButtonsRow.add(buttonUpdateInfo);

        InlineKeyboardButton buttonUpdatePhone = new InlineKeyboardButton();
        buttonUpdatePhone.setText("☎️");
        buttonUpdatePhone.setCallbackData("UpdateUserPhone");
        keyboardButtonsRow.add(buttonUpdatePhone);

        InlineKeyboardButton buttonUpdateEmail = new InlineKeyboardButton();
        buttonUpdateEmail.setText("\uD83D\uDCEB");
        buttonUpdateEmail.setCallbackData("UpdateUserEmail");
        keyboardButtonsRow.add(buttonUpdateEmail);

        InlineKeyboardButton buttonUpdateAgreement = new InlineKeyboardButton();
        buttonUpdateAgreement.setText(botService.getUserByTg(message.getFrom().getId()).getCurrentAgreementSmile());
        buttonUpdateAgreement.setCallbackData("UpdateUserAgreement");
        keyboardButtonsRow.add(buttonUpdateAgreement);

        rowList.add(keyboardButtonsRow);
        inlineKeyboardMarkup.setKeyboard(rowList);

        sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            TovarDto[] tovarArr;
            InlineKeyboardMarkup inlineKeyboardMarkup;
            List<InlineKeyboardButton> keyboardButtonsRow;
            List<List<InlineKeyboardButton>> rowList;
            String messageText;
            ActionInfo action=null;
            int l, r;
            if (message.getText().equals("/start")) {
                try {
                    Long id_telegram = message.getFrom().getId();
                    String name = message.getChat().getUserName();
                    String firstname = message.getChat().getFirstName();
                    String lastname = message.getChat().getLastName();
                    String phone = "";
                    String mail = "";
                    boolean agreement = false;
                    botService.addUser(id_telegram, name, firstname, lastname, phone, mail, agreement);
                    sendMsg(message, "Здравствуйте, " + message.getFrom().getFirstName() + "! Чтобы работать с ботом, необходимо написать в чат свой номер телефона и почту. Затем вы получите доступ к комаднам\nЧтобы посмотреть список команд, введите /help");
                    sendMsg(message, "Я тебя запомнил");
                    ActionInfo actionInfo = new ActionInfo(2,1);
                    actions.put(message.getFrom().getId(),actionInfo);
                }
                catch (HttpServerErrorException e) {
                    sendMsg(message, "/start - одноразовая команда, не вызывайте её");
                    return;
                }

            }
            UsersDto user = botService.getUserByTg(message.getFrom().getId());

            if (actions.get(message.getFrom().getId())!=null && !message.getText().contains("/")) {
                action = actions.get(message.getFrom().getId());
            }
            if (action!=null && user.getPhone()=="" && action.getActionTypeId()==2) {
                if (message.getText().length()==11 && message.getText().chars().allMatch( Character::isDigit )) {
                    boolean agreement =user.getAgreement();
                    botService.changeUser(message.getFrom().getId(), message.getText(), agreement);
                    ActionInfo actionInfo = new ActionInfo(3,1);
                    actions.put(message.getFrom().getId(),actionInfo);
                    sendMsg(message, "Телефон добавлен, теперь введите почту");
                }
                else {
                    sendMsg(message, "Номер телефона неверен, он должен содержать 11 цифр");
                }
            }
            else if (user.getPhone()=="") {
                ActionInfo actionInfo = new ActionInfo(2,1);
                actions.put(message.getFrom().getId(),actionInfo);
                sendMsg(message, "Введите свой номер телефона в следующем сообщении");
            }
            else if (action!=null && user.getMail()=="" && action.getActionTypeId()==3) {
                if (message.getText().contains("@")) {
                    boolean agreement =user.getAgreement();
                    botService.changeUser(message.getText(),message.getFrom().getId(), agreement);
                    actions.remove(message.getFrom().getId());
                    sendMsg(message, "Адрес почты добавлен, теперь вы имеете полный доступ к командам");
                }
                else {
                    sendMsg(message, "Почта неверна, она должна содержать символ @");
                }
            }
            else if (user.getMail()=="") {
                ActionInfo actionInfo = new ActionInfo(3,1);
                actions.put(message.getFrom().getId(),actionInfo);
                sendMsg(message, "Введите свою почту в следующем сообщении");
            }
            else {
                if (action!=null && message.getText().chars().allMatch( Character::isDigit ) && !message.getText().equals("0")) {
                    if (action.getActionTypeId() == 0  ) {
                        try {
                            botService.addToCart(message.getFrom().getId(), action.getTovarId(), Integer.parseInt(message.getText()));
                            sendMsg(message, "Добавил товар №" + action.getTovarId() + " в количесте " + message.getText() + " в корзину");
                            actions.remove(message.getFrom().getId());
                        }
                        catch (HttpClientErrorException e) {
                            if (e.getLocalizedMessage().contains("There is no such quantity of good")) {
                                sendMsg(message, "У нас нет столько товара на складе");
                            }
                            else if (e.getLocalizedMessage().contains("This good is already in car")) {
                                sendMsg(message, "Этот товар уже добавлен в корзину");
                                actions.remove(message.getFrom().getId());
                            }
                        }
                        catch (NumberFormatException e) {
                            sendMsg(message, "Вы не можете добавить в корзину больше 32767 единиц товара потому что: \n1) У вас нет столько денег\n2) У вас нет *СТОЛЬКО* денег\n3) У нас на складе нет столько товара");
                        }
                    } else if (action.getActionTypeId() == 1) {
                        botService.addToRemind(message.getFrom().getId(), (long) action.getTovarId(), Integer.parseInt(message.getText()));
                        sendMsg(message, "Добавил товар №" + action.getTovarId() + " в количесте " + message.getText() + " в избранное");
                        actions.remove(message.getFrom().getId());
                    } else if (action.getActionTypeId() == 2) {
                        String phoneNumber = message.getText();
                        if (phoneNumber.length() == 11) {
                            boolean agreement =botService.getUserByTg(message.getFrom().getId()).getAgreement();
                            botService.changeUser(message.getFrom().getId(), phoneNumber, agreement);
                            actions.remove(message.getFrom().getId());
                            sendMsg(message, "Номер телефона обновлён");
                            sendUserInfo(message);
                        } else {
                            sendMsg(message, "Номер телефона неверен, он должен содержать 11 цифр");
                        }
                    }
                }
                else if (action!=null && action.getActionTypeId()==3) {
                    String email = message.getText();
                    if (email.contains("@")) {
                        boolean agreement =botService.getUserByTg(message.getFrom().getId()).getAgreement();
                        botService.changeUser(email,message.getFrom().getId(), agreement);
                        actions.remove(message.getFrom().getId());
                        sendMsg(message, "Адрес почты обновлён");
                        sendUserInfo(message);
                    }
                    else {
                        sendMsg(message, "Неверный формат почты");
                    }
                }


                else {

                    switch (message.getText()) {

//                        case "/start":
//                            sendMsg(message, "Здравствуйте, " + message.getFrom().getFirstName() + "! Чтобы работать с ботом, необходимо написать в чат свой номер телефона и почту. Затем вы получите доступ к комаднам\nЧтобы посмотреть список команд, введите /help");
//                            Long id_telegram = message.getFrom().getId();
//                            String name = message.getChat().getUserName();
//                            String firstname = message.getChat().getFirstName();
//                            String lastname = message.getChat().getLastName();
//                            String phone = "";
//                            String mail = "";
//                            boolean agreement = false;
//                            botService.addUser(id_telegram, name, firstname, lastname, phone, mail, agreement);
//                            sendMsg(message, "Я тебя запомнил");
//                            ActionInfo actionInfo = new ActionInfo(2,1);
//                            actions.put(message.getFrom().getId(),actionInfo);
//                            break;
                        case "/help":
                            sendMsg(message, "Никто тебе не поможет\nБот создан для интернет-магазина\nДоступные команды:\n" +
                                    "/help - Посмотреть команды\n" +
                                    "/my_info - Посмотреть и изменить мои данные\n" +
                                    "/get_tovar - Посмотреть товары\n" +
                                    "/get_tovar_by_category - Посмотреть товары по категории\n" +
                                    "/get_categories - Посмотреть категории\n" +
                                    "/get_remind - Посмотреть товары в избранном\n" +
                                    "/get_cart - Посмотреть корзину\n" +
                                    "/buy - Купить товары из корзины");
                            break;
                        case "/my_info":
                            sendUserInfo(message);
                            break;
//                    case "/add_user":
//                        Long id_telegram = message.getFrom().getId();
//                        String name = message.getChat().getUserName();
//                        String firstname = message.getChat().getFirstName();
//                        String lastname = message.getChat().getLastName();
//                        String phone = "88005553535";
//                        String mail = "1@gmail.com";
//                        boolean agreement = true;
//                        botService.addUser(id_telegram, name, firstname, lastname, phone, mail, agreement);
//                        sendMsg(message, "Я тебя запомнил");
//                        break;
                        case "/get_tovar":
                            fullTovarArr = botService.getTovar(message.getFrom().getId());
                            sendTovarInfo(message, fullTovarArr,0);
                            break;
                        case "/get_tovar_by_category":
                            CategoryDto[] categoriesArr = botService.getCategories(message.getFrom().getId());
                            inlineKeyboardMarkup = new InlineKeyboardMarkup();
                            rowList = new ArrayList<>();
                            messageText="*Категории:*\n";
                            inlineKeyboardMarkup = new InlineKeyboardMarkup();
                            rowList = new ArrayList<>();
                            messageText="*Категории:*\n";
                            for (int i=0; i< (int)Math.ceil(categoriesArr.length/5.0); i++) {
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
                            //Тут происходит волшебство. getCart выдаёт мне null, если в корзине ничего нет. А getRemind выдаёт мне пустой массив длины 0
                            if (remindArr==null || remindArr.length==0) {
                                sendMsg(message, "Ты не можешь посмотреть ремайнд, если у тебя его нет");
                            }
                            else {
                                sendRemindInfo(message, remindArr);
                            }

                            break;
                        case "/get_cart":
                            TrashDto[] trashArr= botService.getCart(message.getFrom().getId());
                            if (trashArr==null || trashArr.length==0) {
                                sendMsg(message, "Ты не можешь посмотреть корзину, если у тебя её нет");
                            }
                            else {
                                sendCartInfo(message,trashArr);
                            }

                            break;
                        case "/buy":
                            trashArr= botService.getCart(message.getFrom().getId());
                            if (trashArr==null || trashArr.length==0) {
                                sendMsg(message, "Так у тебя корзина пустая, ты что купить собрался");
                            }
                            else {
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
                            }
                            break;
                        default:
                            sendMsg(message, "Бип-буп, я робот-идиот, команда не распознана");
                            break;
                    }
                }
            }
        }
        else if (update.hasCallbackQuery()) {
            String command = update.getCallbackQuery().getData();
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Message message = new Message();
            message.setChat(callbackQuery.getMessage().getChat());
            message.setFrom(callbackQuery.getFrom());
            if (command.startsWith("Next")) {
                answerCallbackQuery(callbackQuery, "Показываю следующие товары");
                sendTovarInfo(message,fullTovarArr, Integer.parseInt(command.substring(5)));
            }
            if (command.equals("UpdateUserInfo")) {
                boolean agreement =botService.getUserByTg(message.getFrom().getId()).getAgreement();
                botService.changeUser(callbackQuery.getFrom().getId(),callbackQuery.getFrom().getUserName(),callbackQuery.getFrom().getFirstName(),callbackQuery.getFrom().getLastName(), agreement);
                answerCallbackQuery(callbackQuery, "Ваша информация обновлена");
                sendUserInfo(message);
            }
            if (command.equals("UpdateUserPhone")) {
                ActionInfo actionInfo = new ActionInfo(2,1);
                actions.put(callbackQuery.getFrom().getId(),actionInfo);
                answerCallbackQuery(callbackQuery, "Напишите ваш актуальный номер телефона");
            }
            if (command.equals("UpdateUserEmail")) {
                ActionInfo actionInfo = new ActionInfo(3,1);
                actions.put(callbackQuery.getFrom().getId(),actionInfo);
                answerCallbackQuery(callbackQuery, "Напишите вашу актуальную почту");
            }
            if (command.equals("UpdateUserAgreement")) {
                boolean agreement =!botService.getUserByTg(message.getFrom().getId()).getAgreement();
                botService.changeUser(callbackQuery.getFrom().getId(),agreement);
                answerCallbackQuery(callbackQuery, "Ваша информация обновлена");
                sendUserInfo(message);
            }
            else if (command.startsWith("Category:")) {
                TovarDto[] tovarArr = botService.getTovarByCategory(callbackQuery.getFrom().getId(), Integer.parseInt(command.substring(9)));
                answerCallbackQuery(callbackQuery, "Показываю товары в категории №"+Integer.parseInt(command.substring(9)));
                sendTovarInfo(message, tovarArr);
            }
            else if (command.startsWith("Remind:")) {
                ActionInfo actionInfo = new ActionInfo(1,Integer.parseInt(command.substring(7)));
                actions.put(callbackQuery.getFrom().getId(),actionInfo);
                answerCallbackQuery(callbackQuery, "Напишите, сколько товара №"+command.substring(7)+" вы хотите добавить в любимое");
            }
            else if (command.startsWith("Cart:")) {
                ActionInfo actionInfo = new ActionInfo(0, Integer.parseInt(command.substring(5)));
                actions.put(callbackQuery.getFrom().getId(), actionInfo);
                answerCallbackQuery(callbackQuery, "Напишите, сколько товара №" + command.substring(5) + " вы хотите добавить в корзину");
            }
            else if (command.startsWith("ChangeCart:")) {
                ActionInfo actionInfo = new ActionInfo(4, Integer.parseInt(command.substring(11)));
                actions.put(callbackQuery.getFrom().getId(), actionInfo);
                answerCallbackQuery(callbackQuery, "Напишите, сколько товара №" + command.substring(11) + " вы хотите купить");
            }
            else if (command.startsWith("DeleteCart:")) {
                botService.removeFromCart(callbackQuery.getFrom().getId(), Long.parseLong(command.substring(11)));
                answerCallbackQuery(callbackQuery, "Товар №" + command.substring(11) + " удалён из корзины");
                TrashDto[] trashArr = botService.getCart(callbackQuery.getFrom().getId());
                sendCartInfo(message, trashArr);
            }
            else if (command.startsWith("ChangeRemind:")) {
                ActionInfo actionInfo = new ActionInfo(4, Integer.parseInt(command.substring(13)));
                actions.put(callbackQuery.getFrom().getId(), actionInfo);
                answerCallbackQuery(callbackQuery, "Напишите, сколько товара №" + command.substring(13) + " вы иметь в избранном");
            }
            else if (command.startsWith("DeleteRemind:")) {
                botService.removeFromRemind(callbackQuery.getFrom().getId(), Long.parseLong(command.substring(13)));
                answerCallbackQuery(callbackQuery, "Товар №" + command.substring(13) + " удалён из избранного");
                RemindDto[] remindArr = botService.getRemind(callbackQuery.getFrom().getId());
                sendRemindInfo(message, remindArr);
            }
            else if (command.equals("NO")) {
                String messageText = "¯'_(ツ)_/¯".replace("'","\\");
                answerCallbackQuery(callbackQuery, "Эта кнопка нужна, чтобы дать вам иллюзию выбора. Если не хотите покупать, просто не нажимайте на Да "+messageText);
            }
            else {
                answerCallbackQuery(callbackQuery, "Ты где этот коллбэк достал?");
                }
            }
        }

}

