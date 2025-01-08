package app.carsharing.dto.rental;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ReturnRentalRequestDto {
    @NotNull(message = "Actual return date can't be null")
    private LocalDate actualDate;
}
