package app.carsharing.service.payment;

import app.carsharing.dto.payment.CreatePaymentRequestDto;
import app.carsharing.dto.payment.PaymentFullResponseDto;
import app.carsharing.dto.payment.PaymentShortResponseDto;
import app.carsharing.dto.payment.PaymentStatusResponseDto;
import app.carsharing.exception.EntityNotFoundException;
import app.carsharing.exception.PaymentException;
import app.carsharing.mapper.PaymentMapper;
import app.carsharing.model.Rental;
import app.carsharing.model.payment.Payment;
import app.carsharing.model.payment.PaymentStatus;
import app.carsharing.model.payment.PaymentType;
import app.carsharing.repository.car.CarRepository;
import app.carsharing.repository.payment.PaymentRepository;
import app.carsharing.repository.rental.RentalRepository;
import app.carsharing.service.payment.strategy.PaymentCalculateStrategy;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final int HOUR_IN_SECONDS = 3600;
    private static final String DOMAIN = "http://localhost:8080";
    private static final String CANCELED_LINK = "/payments/success/{CHECKOUT_SESSION_ID}";
    private static final String SUCCESS_LINK = "/payments/cancel/{CHECKOUT_SESSION_ID}";
    private static final String SESSION_NAME = "Car rental payment session";
    private static final String COMPLETE_SESSION_STATUS = "complete";
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentCalculateStrategy paymentCalculateStrategy;
    private final PaymentMapper paymentMapper;
    private final CarRepository carRepository;

    @Override
    public PaymentShortResponseDto createPayment(CreatePaymentRequestDto requestDto, Long userId) {
        if (paymentRepository.findByRentalIdAndStatusIn(requestDto.getRentalId(),
                Set.of(PaymentStatus.PAID, PaymentStatus.PENDING)).isPresent()) {
            throw new PaymentException("Couldn't create payment with rental id: "
                    + requestDto.getRentalId());
        }
        Rental rental = rentalRepository.findById(requestDto.getRentalId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find rental by id: "
                                + requestDto.getRentalId()));

        rental.setCar(carRepository.findById(rental.getCar().getId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id: "
                        + rental.getCar().getId())
        ));

        if (!rental.getUser().getId().equals(userId)) {
            throw new PaymentException("You don't have permission to this rental");
        }

        String paymentRequestType = requestDto.getPaymentType()
                .toString()
                .toUpperCase();

        Long amountToPay = paymentCalculateStrategy
                .getPaymentByType(paymentRequestType)
                .calculateAmountToPay(rental)
                .longValue();

        Session session = createStripeSession(amountToPay).orElseThrow(
                () -> new PaymentException("An error occurred while creating the payment"));

        Payment payment = new Payment()
                .setRental(rental)
                .setAmountToPay(BigDecimal.valueOf(amountToPay))
                .setSessionId(session.getId())
                .setSession(session.getUrl())
                .setStatus(PaymentStatus.PENDING)
                .setType(PaymentType.valueOf(paymentRequestType));

        return paymentMapper.toShortResponseDto(paymentRepository.save(payment));
    }

    @Override
    public Page<PaymentFullResponseDto> findALlByUserId(Long userId, Pageable pageable) {
        return paymentRepository.findAllByRentalUserId(userId, pageable)
                .map(paymentMapper::toFullResponseDto);
    }

    @Override
    public PaymentStatusResponseDto handleSuccess(String sessionId) {
        try {
            Payment payment = findPaymentBySessionId(sessionId);
            Session session = Session.retrieve(sessionId);
            if (session.getStatus().equals(COMPLETE_SESSION_STATUS)) {
                payment.setStatus(PaymentStatus.PAID);
                paymentRepository.save(payment);
                return paymentMapper.toStatusResponseDto(payment)
                        .setMessage("Successful payment");
            }
            return paymentMapper.toStatusResponseDto(payment)
                    .setMessage("Payment failed");

        } catch (Exception e) {
            throw new PaymentException("An error occurred while creating the payment");
        }
    }

    @Override
    public PaymentStatusResponseDto handleCancel(String sessionId) {
        Payment payment = findPaymentBySessionId(sessionId);
        payment.setStatus(PaymentStatus.CANCELLED);
        return paymentMapper.toStatusResponseDto(paymentRepository.save(payment))
                .setMessage("Payment session canceled");
    }

    private Optional<Session> createStripeSession(Long amountToPay) {
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
                .setSuccessUrl(DOMAIN + SUCCESS_LINK)
                .setCancelUrl(DOMAIN + CANCELED_LINK)
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

    private Payment findPaymentBySessionId(String sessionId) {
        return paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Can't find payment by sessionId: "
                        + sessionId)
        );
    }
}
