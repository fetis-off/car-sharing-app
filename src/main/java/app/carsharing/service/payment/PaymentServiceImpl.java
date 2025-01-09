package app.carsharing.service.payment;

import app.carsharing.dto.payment.CreatePaymentRequestDto;
import app.carsharing.dto.payment.PaymentFullResponseDto;
import app.carsharing.dto.payment.PaymentShortResponseDto;
import app.carsharing.dto.payment.PaymentStatusResponseDto;
import app.carsharing.exception.EntityNotFoundException;
import app.carsharing.exception.PaymentException;
import app.carsharing.mapper.PaymentMapper;
import app.carsharing.model.Rental;
import app.carsharing.model.User;
import app.carsharing.model.payment.Payment;
import app.carsharing.model.payment.PaymentStatus;
import app.carsharing.model.payment.PaymentType;
import app.carsharing.repository.car.CarRepository;
import app.carsharing.repository.payment.PaymentRepository;
import app.carsharing.repository.rental.RentalRepository;
import app.carsharing.service.notification.TelegramNotificationService;
import app.carsharing.service.payment.strategy.PaymentCalculateStrategy;
import app.carsharing.utils.Message;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final String COMPLETE_SESSION_STATUS = "paid";
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentCalculateStrategy paymentCalculateStrategy;
    private final PaymentMapper paymentMapper;
    private final CarRepository carRepository;
    private final TelegramNotificationService notificationService;
    private final StripeService stripeService;

    @Override
    public PaymentShortResponseDto createPayment(CreatePaymentRequestDto requestDto, Long userId) {
        validatePaymentRequest(requestDto, userId);
        Rental rental = prepareRental(requestDto.getRentalId());
        Long amountToPay = calculatePaymentAmount(requestDto.getPaymentType(), rental);

        Session session = createStripeSession(amountToPay);
        Payment payment = savePayment(session, rental, amountToPay, requestDto.getPaymentType());

        return paymentMapper.toShortResponseDto(payment);
    }

    @Override
    public Page<PaymentFullResponseDto> findALlByUserId(Long userId, Pageable pageable) {
        return paymentRepository.findAllByRentalUserId(userId, pageable)
                .map(paymentMapper::toFullResponseDto);
    }

    @Override
    public PaymentStatusResponseDto handleSuccess(String sessionId) {
        try {
            Payment payment = findAndValidatePayment(sessionId);
            updatePaymentStatus(payment, sessionId);
            notifyUserOnSuccess(payment);
            return buildSuccessResponse(payment, "Successful payment");
        } catch (Exception e) {
            throw new PaymentException("An error occurred while handling the payment");
        }
    }

    @Override
    public PaymentStatusResponseDto handleCancel(String sessionId) {
        Payment payment = findPaymentBySessionId(sessionId);
        payment.setStatus(PaymentStatus.CANCELLED);
        return paymentMapper.toStatusResponseDto(paymentRepository.save(payment))
                .setMessage("Payment session canceled");
    }

    private Payment findPaymentBySessionId(String sessionId) {
        return paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Can't find payment by sessionId: "
                        + sessionId)
        );
    }

    private boolean isAbleCreateSession(CreatePaymentRequestDto requestDto) {
        return paymentRepository.findByRentalIdAndStatusIn(requestDto.getRentalId(),
                Set.of(PaymentStatus.PAID, PaymentStatus.PENDING)).isPresent();
    }

    private Rental findRentalById(Long rentalId) {
        return rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find rental by id: "
                                + rentalId));
    }

    private void validatePaymentRequest(CreatePaymentRequestDto requestDto, Long userId) {
        if (!isAbleCreateSession(requestDto)) {
            throw new PaymentException("Couldn't create payment with rental id: "
                    + requestDto.getRentalId());
        }

        Rental rental = findRentalById(requestDto.getRentalId());
        if (!rental.getUser().getId().equals(userId)) {
            throw new PaymentException("You don't have permission to this rental");
        }
    }

    private Rental prepareRental(Long rentalId) {
        Rental rental = findRentalById(rentalId);
        rental.setCar(carRepository.findById(rental.getCar().getId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find car by id: "
                        + rental.getCar().getId())
        ));
        return rental;
    }

    private Long calculatePaymentAmount(PaymentType paymentType, Rental rental) {
        String paymentRequestType = paymentType.toString().toUpperCase();
        return paymentCalculateStrategy
                .getPaymentByType(paymentRequestType)
                .calculateAmountToPay(rental)
                .longValue();
    }

    private Session createStripeSession(Long amountToPay) {
        return stripeService.createStripeSession(amountToPay).orElseThrow(
                () -> new PaymentException("An error occurred while creating the payment")
        );
    }

    private Payment savePayment(Session session,
                                Rental rental,
                                Long amountToPay,
                                PaymentType paymentType) {
        Payment payment = new Payment()
                .setRental(rental)
                .setAmountToPay(BigDecimal.valueOf(amountToPay))
                .setSessionId(session.getId())
                .setSession(session.getUrl())
                .setStatus(PaymentStatus.PENDING)
                .setType(PaymentType.valueOf(paymentType.toString().toUpperCase()));

        return paymentRepository.save(payment);
    }

    private Payment findAndValidatePayment(String sessionId) {
        Payment payment = findPaymentBySessionId(sessionId);
        if (payment == null) {
            throw new PaymentException("Payment with session ID " + sessionId + " not found");
        }
        return payment;
    }

    private void updatePaymentStatus(Payment payment, String sessionId) throws Exception {
        Session session = Session.retrieve(sessionId);
        if (COMPLETE_SESSION_STATUS.equals(session.getStatus())) {
            payment.setStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);
        }
    }

    private void notifyUserOnSuccess(Payment payment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return;
        }

        if (user.getTgChatId() != null) {
            notificationService.sendNotification(
                    user.getTgChatId(),
                    Message.getSuccessfulPaymentMessageForTg(payment)
            );
        }
    }

    private PaymentStatusResponseDto buildSuccessResponse(Payment payment, String message) {
        return paymentMapper.toStatusResponseDto(payment).setMessage(message);
    }
}
