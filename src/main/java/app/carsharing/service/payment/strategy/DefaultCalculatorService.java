package app.carsharing.service.payment.strategy;

import static java.time.temporal.ChronoUnit.DAYS;

import app.carsharing.model.Rental;
import app.carsharing.model.payment.PaymentType;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class DefaultCalculatorService implements PaymentCalculateService {
    private static final String PAYMENT_TYPE = PaymentType.PAYMENT.name();

    @Override
    public String getPaymentType() {
        return PAYMENT_TYPE;
    }

    @Override
    public BigDecimal calculateAmountToPay(Rental rental) {
        Long countOfDays = DAYS.between(rental.getRentalDate(), rental.getReturnDate());
        return rental.getCar().getDailyFee().multiply(BigDecimal.valueOf(countOfDays));
    }
}
