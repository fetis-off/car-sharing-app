package app.carsharing.dto.car;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CarShortResponseDto {
    private Long id;
    private String model;
    private String brand;
    private String carType;
}
