package app.carsharing.dto.payment;

import lombok.Data;

@Data
public class PaymentShortResponseDto {
    private String sessionId;
    private String sessionUrl;
}
