package app.carsharing.bot;

import app.carsharing.exception.NotificatonException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Getter
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final String botToken;
    private final String botUsername;

    public void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new NotificatonException(
                    String.format("Cant send message: %s in telegram", text));
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().getText().equals("/getChatId")) {
            Long chatId = update.getMessage().getChatId();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText("Your chat id is: " + chatId);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new NotificatonException("Cant send chat id to user");
            }
        }
    }
}

