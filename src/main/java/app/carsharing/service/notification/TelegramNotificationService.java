package app.carsharing.service.notification;

import app.carsharing.bot.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private final TelegramBot telegramBot;

    @Override
    public void sendNotification(Long chatId, String message) {
        telegramBot.sendMessage(chatId, message);
    }
}
