package app.carsharing.dto.car;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateCarRequestDto {
    @NotBlank(message = "Car model can't be null")
    @Length(max = 50, message = "Car model can't be longer than 50 symbols")
    private String model;

    @NotBlank(message = "Car brand can't be null")
    @Length(max = 50, message = "Car brand can't be longer than 50 symbols")
    private String brand;

    @NotBlank(message = "Car type can't be null")
    @Length(max = 50, message = "Car type can't be longer than 50 symbols")
    private String carType;

    @NotNull(message = "The inventory field can't be null")
    @Positive(message = "The inventory field should be positive")
    private int inventory;

    @NotNull(message = "Daily fee field can't be null")
    @Positive(message = "Daily fee field should be positive")
    private BigDecimal dailyFee;
}
