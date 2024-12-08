package app.carsharing.mapper;

import app.carsharing.config.MapperConfig;
import app.carsharing.dto.car.CarFullResponseDto;
import app.carsharing.dto.car.CarShortResponseDto;
import app.carsharing.dto.car.CreateCarRequestDto;
import app.carsharing.model.car.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    CarFullResponseDto toCarFullResponseDto(Car car);

    Car toCar(CreateCarRequestDto requestDto);

    CarShortResponseDto toCarShortResponseDto(Car car);

    void updateCar(CreateCarRequestDto requestDto, @MappingTarget Car car);
}
