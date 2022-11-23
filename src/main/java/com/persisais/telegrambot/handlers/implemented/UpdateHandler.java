//package com.persisais.telegrambot.handlers.implemented;
//
//import com.persisais.telegrambot.handlers.Handler;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.meta.api.objects.Update;
//
//import java.util.LinkedHashSet;
//import java.util.Set;
//
////@Component
//public class UpdateHandler implements Handler {
//
//    private final MessageHandler messageHandler = new MessageHandler();
//    private final CallbackHandler callbackHandler = new CallbackHandler();
//
//    private Set<Handler> getHandlers() {
//        Set<Handler> result = new LinkedHashSet<>();
//        result.add(messageHandler);
//        result.add(callbackHandler);
//        return result;
//    }
//
//    @Override
//    public boolean supports(Update update) {
//        return true;
//    }
//
//    @Override
//    public void handle(Update update) {
//        handleUpdate(update);
////        try{
////            handleUpdate(update);
////        }
////        catch (Exception e) {
////            System.out.println(e);
////        }
//    }
//
//    private void handleUpdate(Update update) {
//        getHandlers().stream()
//                .filter(handler -> handler.supports(update))
//                .findFirst()
//                .ifPresent(handler -> handler.handle(update));
//    }
//}
