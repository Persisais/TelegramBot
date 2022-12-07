//package com.persisais.telegrambot.service;
//
//import com.persisais.telegrambot.bot.Bot;
//import com.persisais.telegrambot.model.TovarDto;
//import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
//import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
//import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class Sender {
//    private final String emptyImage = "https://www.ponycorral.ca/wp-content/uploads/2015/07/placeholder-image-1000x1000-150x150.png";
//
//    @Autowired
//    private BotService botService;
//
//    public void sendMsg(Message message, String text) {
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.enableMarkdown(true);
//        sendMessage.setChatId(message.getChatId().toString());
//        sendMessage.setReplyToMessageId(message.getMessageId());
//        sendMessage.setText(text);
//        try {
//            bot.execute(sendMessage);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void sendInlineKeyboardMsg(Message message, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.enableMarkdown(true);
//        sendMessage.setChatId(message.getChatId().toString());
//        sendMessage.setReplyToMessageId(message.getMessageId());
//        sendMessage.setText(text);
//        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
//
//        try {
//            bot.execute(sendMessage);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void sendMediaGroup(Message message, List<InputMedia> media) {
//        SendMediaGroup sendMediaGroup = new SendMediaGroup();
//        sendMediaGroup.setChatId(message.getChatId().toString());
//        sendMediaGroup.setReplyToMessageId(message.getMessageId());
//        sendMediaGroup.setMedias(media);
//        try {
//            bot.execute(sendMediaGroup);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void sendTovarInfo(Message message, TovarDto[] tovarArr) {
//        int r, l;
//        for (int i=0; i< (int)Math.ceil(tovarArr.length/5.0); i++) {
//            String messageText="";
//            l= 5*i;
//            r= Math.min(l + 5, tovarArr.length);
//            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//            List<InlineKeyboardButton> keyboardButtonsRow= new ArrayList<>();
//            List<InlineKeyboardButton> keyboardButtonsSecondRow= new ArrayList<>();
//            List<InputMedia> media = new ArrayList<>();
//            for (int j=l; j<r; j++) {
//                messageText+=tovarArr[j]+"\n----------------\n";
//                InlineKeyboardButton button = new InlineKeyboardButton();
//                InlineKeyboardButton buttonSecond = new InlineKeyboardButton();
//                if (tovarArr[j].getQuantity_in_stock()>0) {
//                    button.setText(tovarArr[j].getId().toString());
//                    button.setCallbackData(tovarArr[j].getId().toString());
//                    keyboardButtonsRow.add(button);
//                }
//                buttonSecond.setText("❤️"+tovarArr[j].getId().toString());
//                buttonSecond.setCallbackData("fav"+tovarArr[j].getId().toString());
//                keyboardButtonsSecondRow.add(buttonSecond);
//
//
//
//                InputMedia photo = new InputMediaPhoto();
//                if (tovarArr[j].getPhoto()!=null) {
//                    String pathname ="images/";
//                    byte[] image = botService.getTovarImage(message.getFrom().getId(), tovarArr[j].getId());
//                    try {
//                        Files.write(Paths.get(pathname+tovarArr[j].getId()+".png"), image);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    photo.setMedia(new File(pathname+tovarArr[j].getId()+".png"), tovarArr[j].getId().toString());
////                                try {
////                                    photo.setMedia( new FileInputStream("images/"+tovarArr[j].getId()+".png"), tovarArr[j].getId().toString());
////                                } catch (FileNotFoundException e) {
////                                    throw new RuntimeException(e);
////                                }
//                }
//                else {
//                    photo.setMedia(emptyImage);
//                }
//                photo.setMediaName(tovarArr[j].getId().toString());
//                photo.setCaption(tovarArr[j].toString());
//                media.add(photo);
//            }
//            if (r!=tovarArr.length) {
//                InlineKeyboardButton button = new InlineKeyboardButton();
//                button.setText("-->");
//                button.setCallbackData("next");
//                keyboardButtonsRow.add(button);
//            }
//            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
//            rowList.add(keyboardButtonsRow);
//            rowList.add(keyboardButtonsSecondRow);
//            inlineKeyboardMarkup.setKeyboard(rowList);
//            sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
//            sendMediaGroup(message, media);
//        }
//    }
//    public void answerCallbackQuery(CallbackQuery callbackQuery, String text) {
//        AnswerCallbackQuery answer = new AnswerCallbackQuery();
//        answer.setCallbackQueryId(callbackQuery.getId());
//        answer.setText(text);
//        answer.setShowAlert(true);
//        try {
//            bot.execute(answer);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//}
