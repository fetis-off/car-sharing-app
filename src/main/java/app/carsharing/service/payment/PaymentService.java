package app.carsharing.service.payment;

import app.carsharing.dto.payment.CreatePaymentRequestDto;
import app.carsharing.dto.payment.PaymentFullResponseDto;
import app.carsharing.dto.payment.PaymentShortResponseDto;
import app.carsharing.dto.payment.PaymentStatusResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentShortResponseDto createPayment(CreatePaymentRequestDto requestDto, Long userId);

    Page<PaymentFullResponseDto> findALlByUserId(Long userId, Pageable pageable);

    PaymentStatusResponseDto handleSuccess(String sessionId);

    PaymentStatusResponseDto handleCancel(String sessionId);
}
