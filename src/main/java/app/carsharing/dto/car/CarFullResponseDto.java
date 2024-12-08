package app.carsharing.dto.car;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CarFullResponseDto {
    private Long id;
    private String model;
    private String brand;
    private String carType;
    private int inventory;
    private BigDecimal dailyFee;
}
