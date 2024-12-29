package app.carsharing.repository.payment;

import app.carsharing.model.User;
import app.carsharing.model.payment.Payment;
import app.carsharing.model.payment.PaymentStatus;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRentalIdAndStatusIn(Long rentalId, Set<PaymentStatus> status);

    Page<Payment> findAllByRentalUserId(Long id, Pageable pageable);

    Optional<Payment> findBySessionId(String sessionId);

    Optional<Payment> findByStatus(PaymentStatus status);

    boolean existsByRentalUserAndStatus(User user, PaymentStatus status);
}
