package app.carsharing.dto.payment;

import app.carsharing.model.payment.PaymentStatus;
import app.carsharing.model.payment.PaymentType;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentFullResponseDto {
    private Long id;
    private String sessionUrl;
    private String sessionId;
    private BigDecimal amountToPay;
    private Long rentalId;
    private PaymentStatus status;
    private PaymentType type;
}
