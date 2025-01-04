package app.carsharing.service.notification;

public interface NotificationService {
    void sendNotification(Long chatId, String message);
}
