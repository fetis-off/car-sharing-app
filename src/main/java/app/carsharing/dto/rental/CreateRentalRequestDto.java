package app.carsharing.dto.rental;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateRentalRequestDto {
    @NotNull(message = "Rental date can't be null")
    private LocalDate rentalDate;

    @NotNull(message = "Return date can't be null")
    private LocalDate returnDate;

    @NotNull(message = "Car id can't be null")
    @Positive(message = "Car id should be positive")
    private Long carId;
}
