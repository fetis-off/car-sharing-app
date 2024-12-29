package app.carsharing.dto.payment;

import app.carsharing.model.payment.PaymentStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PaymentStatusResponseDto {
    private String message;
    private String sessionId;
    private PaymentStatus status;
}
