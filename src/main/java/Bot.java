import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "FMv0.000003";
    }

    @Override
    public String getBotToken() {
        return "5463822852:AAF1k_KHQyWy2P9CKv0R6Z-X298TRb-XRSI";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            //message.setChatId(update.getMessage().getChatId().toString());
            System.out.println(update.getMessage().getText());
            sendMsg(message, "Бип-буп, я робот-идиот, команда не распознана");
            //message.setText("Beep-boop, I'm am an idiot-robot, i don't know that command");
            //message.setText(update.getMessage().getText());
            /*
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

             */
        }
    }
    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
