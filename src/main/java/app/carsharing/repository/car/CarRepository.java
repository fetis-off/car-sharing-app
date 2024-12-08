package app.carsharing.repository.car;

import app.carsharing.model.car.Car;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findById(Long id);
}
