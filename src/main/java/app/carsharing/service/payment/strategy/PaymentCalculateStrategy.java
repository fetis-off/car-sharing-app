package app.carsharing.service.payment.strategy;

import app.carsharing.exception.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentCalculateStrategy {
    private final List<PaymentCalculateService> paymentHandlers;

    public PaymentCalculateService getPaymentByType(String paymentType) {
        for (PaymentCalculateService service : paymentHandlers) {
            if (service.getPaymentType().equals(paymentType)) {
                return service;
            }
        }
        throw new EntityNotFoundException(
                "No payment service found for payment type: "
                        + paymentType);
    }
}
