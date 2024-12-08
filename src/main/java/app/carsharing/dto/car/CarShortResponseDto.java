package app.carsharing.dto.car;

import lombok.Data;

@Data
public class CarShortResponseDto {
    private Long id;
    private String model;
    private String brand;
    private String carType;
}
