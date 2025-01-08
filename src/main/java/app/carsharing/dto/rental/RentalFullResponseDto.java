package app.carsharing.dto.rental;

import app.carsharing.dto.car.CarFullResponseDto;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RentalFullResponseDto {
    private Long id;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private CarFullResponseDto car;
    private Long userId;
}
