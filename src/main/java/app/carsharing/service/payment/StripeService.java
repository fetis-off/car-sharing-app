package app.carsharing.service.payment;

import app.carsharing.exception.PaymentException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    private static final int HOUR_IN_SECONDS = 3600;
    private static final String CANCELED_LINK = "/payments/success/{CHECKOUT_SESSION_ID}";
    private static final String SUCCESS_LINK = "/payments/cancel/{CHECKOUT_SESSION_ID}";
    private static final String SESSION_NAME = "Car rental payment session";
    private final String domain;

    public StripeService() {
        Dotenv dotenv = Dotenv.configure()
                .filename(".env")
                .load();
        this.domain = dotenv.get("DOMAIN");
    }

    public Optional<Session> createStripeSession(Long amountToPay) {
        SessionCreateParams params = buildSessionCreateParams(amountToPay);
        try {
            return Optional.of(Session.create(params));
        } catch (Exception e) {
            throw new PaymentException("An error occurred while creating the payment");
        }
    }

    private SessionCreateParams buildSessionCreateParams(Long amountToPay) {
        return SessionCreateParams.builder()
                .setExpiresAt(Instant.now().getEpochSecond() + HOUR_IN_SECONDS)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(domain + SUCCESS_LINK)
                .setCancelUrl(domain + CANCELED_LINK)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountToPay * 100)
                                                .setProductData(
                                                        SessionCreateParams
                                                                .LineItem.PriceData
                                                                .ProductData.builder()
                                                                .setName(SESSION_NAME)
                                                                .build())
                                                .build())
                                .build())
                .build();
    }
}
