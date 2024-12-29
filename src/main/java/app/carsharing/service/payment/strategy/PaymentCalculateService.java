package app.carsharing.service.payment.strategy;

import app.carsharing.model.Rental;
import java.math.BigDecimal;

public interface PaymentCalculateService {
    String getPaymentType();

    BigDecimal calculateAmountToPay(Rental rental);
}
