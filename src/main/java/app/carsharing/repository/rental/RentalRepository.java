package app.carsharing.repository.rental;

import app.carsharing.model.Rental;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface RentalRepository extends JpaRepository<Rental, Long>,
        JpaSpecificationExecutor<Rental> {

    @EntityGraph(attributePaths = {"car"})
    Optional<Rental> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT r FROM Rental r JOIN FETCH r.car c JOIN FETCH r.user u "
            + "WHERE r.actualDate IS NULL "
            + "AND r.returnDate <= :currentDate "
            + "AND r.isDeleted = false")
    List<Rental> findOverdueRentals(LocalDate currentDate);

    Optional<Rental> findById(Long id);
}
