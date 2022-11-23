//package com.persisais.telegrambot.handlers.implemented;
//
//import com.persisais.telegrambot.bot.Bot;
//import com.persisais.telegrambot.handlers.Handler;
//import com.persisais.telegrambot.memory.ActionInfo;
//import com.persisais.telegrambot.model.CategoryDto;
//import com.persisais.telegrambot.model.RemindDto;
//import com.persisais.telegrambot.model.TovarDto;
//import com.persisais.telegrambot.model.TrashDto;
//import com.persisais.telegrambot.service.BotService;
//import com.persisais.telegrambot.service.Sender;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.HttpServerErrorException;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
//import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
//
//import java.util.ArrayList;
//import java.util.List;
//
////@Component
//public class MessageHandler implements Handler {
//
////    @Autowired
//    private BotService botService;
////    @Autowired
//    private Sender sender;
////    @Autowired
//    private Bot bot;
//
//    @Override
//    public boolean supports(Update update) {
//        return update.hasMessage() && update.getMessage().hasText();
//    }
//
//
//    @Override
//    public void handle(Update update) {
//        System.out.println("Сообщение поймал");
//        Message message = update.getMessage();
//        TovarDto[] tovarArr;
//        InlineKeyboardMarkup inlineKeyboardMarkup;
//        List<InlineKeyboardButton> keyboardButtonsRow;
//        List<List<InlineKeyboardButton>> rowList;
//        String messageText;
//        int l, r;
//
//        if (bot.actions.get(message.getFrom().getId())!=null && message.getText().chars().allMatch( Character::isDigit )) {
//            ActionInfo action = bot.actions.get(message.getFrom().getId());
//            try {
//                if (action.getActionTypeId()==0) {
//                    botService.addToCart(message.getFrom().getId(),action.getTovarId(),Integer.parseInt(message.getText()));
//                    sender.sendMsg(message, "Добавил товар №"+action.getTovarId()+" в количесте "+message.getText()+" в корзину");
//                    bot.actions.remove(message.getFrom().getId());
//                }
//                else if (action.getActionTypeId()==1) {
//                    botService.addToRemind(message.getFrom().getId(), action.getTovarId(), Integer.parseInt(message.getText()));
//                    sender.sendMsg(message, "Добавил товар №" + action.getTovarId() + " в количесте " + message.getText() + " в избранное");
//                    bot.actions.remove(message.getFrom().getId());
//                }
//            }
//            catch (HttpServerErrorException e) {
//                sender.sendMsg(message,"У нас нет столько товара на складе");
//            }
//            catch (NumberFormatException e) {
//                sender.sendMsg(message,"Вы не можете добавить в корзину больше 32767 единиц товара потому что: \n1) У вас нет столько денег\n2) У вас нет *СТОЛЬКО* денег\n3) У нас на складе нет столько товара");
//            }
//
//
//        }
//        else {
//            switch (message.getText()) {
//
//                case "/start":
//                    sender.sendMsg(message, "Здравствуйте, " + message.getFrom().getFirstName() + "! Чтобы посмотреть список команд, введите /help");
//                    break;
//                case "/help":
//                    sender.sendMsg(message, "Никто тебе не поможет\nБот создан для интернет-магазина\nДоступные команды:\n" +
//                            "/start - начать работу\n/help - помощь\n/setting - настройки\n/random - получить случайное число от 1 до 100");
//                    break;
//                case "/my_info":
//                    messageText = botService.getUserByTg(message.getFrom().getId()).toString();
//                    inlineKeyboardMarkup = new InlineKeyboardMarkup();
//                    rowList = new ArrayList<>();
//                    keyboardButtonsRow= new ArrayList<>();
//                    InlineKeyboardButton buttonUpdateInfo = new InlineKeyboardButton();
//                    buttonUpdateInfo.setText("Обновить");
//                    buttonUpdateInfo.setCallbackData("UpdateUserInfo:"+message.getFrom().getId());
//                    keyboardButtonsRow.add(buttonUpdateInfo);
//
//                    InlineKeyboardButton buttonUpdatePhone = new InlineKeyboardButton();
//                    buttonUpdatePhone.setText("☎️");
//                    buttonUpdatePhone.setCallbackData("UpdateUserPhone:"+message.getFrom().getId());
//                    keyboardButtonsRow.add(buttonUpdatePhone);
//
//                    InlineKeyboardButton buttonUpdateEmail = new InlineKeyboardButton();
//                    buttonUpdateEmail.setText("\uD83D\uDCEB");
//                    buttonUpdateEmail.setCallbackData("UpdateUserEmail:"+message.getFrom().getId());
//                    keyboardButtonsRow.add(buttonUpdateEmail);
//
//                    InlineKeyboardButton buttonUpdateAgreement = new InlineKeyboardButton();
//                    buttonUpdateAgreement.setText(botService.getUserByTg(message.getFrom().getId()).getCurrentAgreementSmile());
//                    buttonUpdateAgreement.setCallbackData("UpdateUserAgreement:"+message.getFrom().getId());
//                    keyboardButtonsRow.add(buttonUpdateAgreement);
//
//                    rowList.add(keyboardButtonsRow);
//                    inlineKeyboardMarkup.setKeyboard(rowList);
//
//                    sender.sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
//                    break;
//                case "/random":
//                    sender.sendMsg(message, String.valueOf((int) (Math.random() * 100 + 1)));
//                    break;
//                case "/add_user":
//                    Long id_telegram = message.getFrom().getId();
//                    String name = message.getChat().getUserName();
//                    String firstname = message.getChat().getFirstName();
//                    String lastname = message.getChat().getLastName();
//                    String phone = "88005553535";
//                    String mail = "1@gmail.com";
//                    boolean agreement = true;
//                    botService.addUser(id_telegram, name, firstname, lastname, phone, mail, agreement);
//                    sender.sendMsg(message, "Я тебя запомнил");
//                    break;
//                case "/get_tovar":
//                    tovarArr = botService.getTovar(message.getFrom().getId());
//                    sender.sendTovarInfo(message, tovarArr);
//                    break;
//                case "/get_tovar_by_category":
//                    CategoryDto[] categoriesArr = botService.getCategories(message.getFrom().getId());
//                    inlineKeyboardMarkup = new InlineKeyboardMarkup();
//                    rowList = new ArrayList<>();
//                    messageText="*Категории:*\n";
//                    for (int i=0; i< (int)Math.ceil(categoriesArr.length/5.0); i++) {
//                        System.out.println(i);
//                        l= 5*i;
//                        r= Math.min(l + 5, categoriesArr.length);
//                        keyboardButtonsRow= new ArrayList<>();
//                        rowList = new ArrayList<>();
//                        for (int j=l; j<r; j++) {
//                            messageText+=categoriesArr[j]+"\n----------------\n";
//                            InlineKeyboardButton button = new InlineKeyboardButton();
//                            button.setText(categoriesArr[j].getName());
//                            button.setCallbackData("Category:"+categoriesArr[j].getId().toString());
//                            keyboardButtonsRow.add(button);
//                        }
//                        rowList.add(keyboardButtonsRow);
//                    }
//                    inlineKeyboardMarkup.setKeyboard(rowList);
//                    sender.sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
//                    break;
//
//                case "/get_categories":
//                    CategoryDto[] categoryArr =botService.getCategories(message.getFrom().getId());
//                    messageText = "*Категории:*";
//                    for (CategoryDto category: categoryArr) {
//                        messageText += category+"\n----------------\n";
//                    }
//                    sender.sendMsg(message, messageText);
//                    break;
//                case "/get_remind":
//                    RemindDto[] remindArr = botService.getRemind(message.getFrom().getId());
//                    for (RemindDto remind : remindArr) {
//                        sender.sendMsg(message, remind.toString());
//                    }
//                    break;
//                case "/get_cart":
//                    TrashDto[] trashArr= botService.getCart(message.getFrom().getId());
//                    messageText ="*Ваша корзина*:\n";
//                    for (TrashDto trash : trashArr) {
//                        messageText+=trash+"\n----------------\n";
//                    }
//                    sender.sendMsg(message, messageText);
//                    break;
//                case "/buy":
//                    trashArr= botService.getCart(message.getFrom().getId());
//                    messageText ="*Ваша корзина*:\n";
//                    for (TrashDto trash : trashArr) {
//                        messageText+=trash+"\n----------------\n";
//                    }
//                    inlineKeyboardMarkup = new InlineKeyboardMarkup();
//                    keyboardButtonsRow = new ArrayList<>();
//                    rowList = new ArrayList<>();
//                    InlineKeyboardButton buttonYes = new InlineKeyboardButton();
//                    InlineKeyboardButton buttonNo = new InlineKeyboardButton();
//                    messageText+="\n[Ссылочка, которая не работает](http://localhost:8080/buy/1675364273)";
//                    messageText+="\n`http://localhost:8080/buy/"+message.getFrom().getId()+"`";
//                    //buttonYes.setUrl("http://localhost:8080/buy/"+message.getFrom().getId());
//
//                    //buttonYes.setUrl("yandex.ru");
//                    buttonYes.setText("Да");
//                    buttonYes.setCallbackData("YES");
//                    buttonNo.setText("Нет");
//                    buttonNo.setCallbackData("NO");
//                    keyboardButtonsRow.add(buttonYes);
//                    keyboardButtonsRow.add(buttonNo);
//                    rowList.add(keyboardButtonsRow);
//                    inlineKeyboardMarkup.setKeyboard(rowList);
//                    sender.sendInlineKeyboardMsg(message, messageText,inlineKeyboardMarkup);
//
//
//                    //sendMsg(message, messageText);
//                    break;
//                default:
//                    sender.sendMsg(message, "Бип-буп, я робот-идиот, команда не распознана");
//                    break;
//            }
//        }
//    }
//}
