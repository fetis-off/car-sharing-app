package app.carsharing.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTgChatId {
    @NotNull(message = "Telegram chat ID can't be null")
    private Long tgChatId;
}
