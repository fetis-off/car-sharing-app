package app.carsharing.dto.car;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateCarInventoryDto {
    @NotNull
    @Positive(message = "Inventory field must be positive number")
    private int inventory;
}
