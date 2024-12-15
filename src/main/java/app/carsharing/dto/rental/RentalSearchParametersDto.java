package app.carsharing.dto.rental;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RentalSearchParametersDto {
    private String userId;

    @NotNull(message = "IsActive can't be null")
    private String isActive;
}
