package app.carsharing.dto.car;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateCarInventoryDto {
    @Positive(message = "Inventory field must be positive number")
    private int inventory;
}
