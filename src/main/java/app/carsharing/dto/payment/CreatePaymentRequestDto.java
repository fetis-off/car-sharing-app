package app.carsharing.dto.payment;

import app.carsharing.model.payment.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreatePaymentRequestDto {
    @NotNull(message = "Rental id can't be null")
    @Positive(message = "Rental id should be positive")
    private Long rentalId;
    
    private PaymentType paymentType;
}
