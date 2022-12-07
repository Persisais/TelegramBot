//package com.persisais.telegrambot.handlers.implemented;
//
//import com.persisais.telegrambot.bot.Bot;
//import com.persisais.telegrambot.service.BotService;
//import com.persisais.telegrambot.handlers.Handler;
//import com.persisais.telegrambot.memory.ActionInfo;
//import com.persisais.telegrambot.model.TovarDto;
//import com.persisais.telegrambot.service.Sender;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
//import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
//import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//
////@Component
//public class CallbackHandler implements Handler {
//
////    @Autowired
//    private BotService botService;
////    @Autowired
//    private Sender sender;
////    @Autowired
//    private Bot bot;
//    private final String emptyImage = "https://www.ponycorral.ca/wp-content/uploads/2015/07/placeholder-image-1000x1000-150x150.png";
//
//
//
//    @Override
//    public boolean supports(Update update) {
//        return update.hasCallbackQuery();
//    }
//
//    @Override
//    public void handle(Update update) {
//        CallbackQuery callbackQuery = update.getCallbackQuery();
//        String command = update.getCallbackQuery().getData();
//        if (command.equals("next")) {
//            sender.answerCallbackQuery(callbackQuery, "Показываю следующие товары (нет)");
//        }
//        else if (command.startsWith("Category:")) {
//            TovarDto[] tovarArr = botService.getTovarByCategory(callbackQuery.getFrom().getId(), Integer.parseInt(command.substring(9)));
//            Message message = new Message();
//            message.setChat(callbackQuery.getMessage().getChat());
//            message.setFrom(callbackQuery.getFrom());
//            sendTovarInfo(message, tovarArr);
//            sender.answerCallbackQuery(callbackQuery, "Показываю товары в категории №"+Integer.parseInt(command.substring(9)));
//        }
//
//        else if (command.startsWith("fav")) {
//            //botService.addToRemind(callbackQuery.getFrom().getId(),Integer.parseInt(command.substring(3)), 1);
//            ActionInfo actionInfo = new ActionInfo(1,Integer.parseInt(command.substring(3)));
////            bot.actions.put(callbackQuery.getFrom().getId(),actionInfo);
//            sender.answerCallbackQuery(callbackQuery, "Напишите, сколько товара №"+command.substring(3)+" вы хотите добавить в любимое");
//
//        }
//        else {
//            //botService.addToCart(callbackQuery.getFrom().getId(),Integer.parseInt(command),1);
//            ActionInfo actionInfo = new ActionInfo(0,Integer.parseInt(command));
////            bot.actions.put(callbackQuery.getFrom().getId(),actionInfo);
//            sender.answerCallbackQuery(callbackQuery, "Напишите, сколько товара №"+command+" вы хотите добавить в корзину");
//
//        }
//    }
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
//            sender.sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
//            sender.sendMediaGroup(message, media);
//        }
//    }
//
//
//}
