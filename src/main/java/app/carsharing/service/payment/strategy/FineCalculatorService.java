package app.carsharing.service.payment.strategy;

import static java.time.temporal.ChronoUnit.DAYS;

import app.carsharing.model.Rental;
import app.carsharing.model.payment.PaymentType;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class FineCalculatorService implements PaymentCalculateService {
    private static final double FINE_MULTIPLIER = 1.2;
    private static final String PAYMENT_TYPE = PaymentType.FINE.name();

    @Override
    public String getPaymentType() {
        return PAYMENT_TYPE;
    }

    @Override
    public BigDecimal calculateAmountToPay(Rental rental) {
        Long countOfDays = DAYS.between(rental.getRentalDate(), rental.getActualDate());
        return rental.getCar().getDailyFee()
                .multiply(BigDecimal.valueOf(countOfDays))
                .multiply(BigDecimal.valueOf(FINE_MULTIPLIER));
    }
}
