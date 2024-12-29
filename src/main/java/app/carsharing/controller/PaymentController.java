package app.carsharing.controller;

import app.carsharing.dto.payment.CreatePaymentRequestDto;
import app.carsharing.dto.payment.PaymentFullResponseDto;
import app.carsharing.dto.payment.PaymentShortResponseDto;
import app.carsharing.dto.payment.PaymentStatusResponseDto;
import app.carsharing.model.User;
import app.carsharing.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payment controller", description = "Endpoints for payments operations")
public class PaymentController {
    private final PaymentService paymentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Create a new payment",
            description = "Allows the customer to create a new payment")
    public PaymentShortResponseDto createPayment(@AuthenticationPrincipal User user,
                                                 @RequestBody @Valid
                                                 CreatePaymentRequestDto requestDto) {
        return paymentService.createPayment(requestDto, user.getId());
    }

    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    @GetMapping
    @Operation(summary = "Find all payments",
            description = "Returns a page of payments by user id")
    public Page<PaymentFullResponseDto> findAllPayments(@AuthenticationPrincipal User user,
                                                        @Positive Long userId,
                                                        @PageableDefault Pageable pageable) {
        if (user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            return paymentService.findALlByUserId(userId, pageable);
        }
        return paymentService.findALlByUserId(user.getId(), pageable);
    }

    @GetMapping("/success/{sessionId}")
    @Operation(summary = "Handle success payment",
            description = "Returns a message from success payment")
    public PaymentStatusResponseDto handleSuccess(@PathVariable @NotBlank String sessionId) {
        return paymentService.handleSuccess(sessionId);
    }

    @GetMapping("/cancel/{sessionId}")
    @Operation(summary = "Handle canceled payment",
            description = "Returns a message from canceled payment")
    public PaymentStatusResponseDto handleCancel(@PathVariable @NotBlank String sessionId) {
        return paymentService.handleCancel(sessionId);
    }
}
