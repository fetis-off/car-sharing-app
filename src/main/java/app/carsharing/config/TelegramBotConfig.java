package app.carsharing.config;

import app.carsharing.bot.TelegramBot;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {
    private final String botToken;
    private final String botUsername;

    public TelegramBotConfig() {
        Dotenv dotenv = Dotenv.configure()
                .filename(".env")
                .load();
        this.botToken = dotenv.get("TELEGRAM_BOT_TOKEN");
        this.botUsername = dotenv.get("TELEGRAM_BOT_USERNAME");
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() {
        try {
            return new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            throw new IllegalStateException("Failed to create TelegramBotsApi", e);
        }
    }

    @Bean
    public TelegramBot telegramBot(TelegramBotsApi telegramBotsApi) {
        try {
            var bot = new TelegramBot(botToken, botUsername);
            telegramBotsApi.registerBot(bot);
            return bot;
        } catch (TelegramApiException e) {
            throw new IllegalStateException("Failed to register Telegram bot", e);
        }
    }
}

