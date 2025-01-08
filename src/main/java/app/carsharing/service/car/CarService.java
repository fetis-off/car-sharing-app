package app.carsharing.service.car;

import app.carsharing.dto.car.CarFullResponseDto;
import app.carsharing.dto.car.CarShortResponseDto;
import app.carsharing.dto.car.CreateCarRequestDto;
import app.carsharing.dto.car.UpdateCarInventoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CarService {
    CarFullResponseDto createCar(CreateCarRequestDto requestDto);

    Page<CarFullResponseDto> findAllCarsFull(Pageable pageable);

    Page<CarShortResponseDto> findAllCarsBrief(Pageable pageable);

    CarFullResponseDto updateCar(Long id, CreateCarRequestDto requestDto);

    CarFullResponseDto updateCarInventory(Long id, UpdateCarInventoryDto requestDto);

    CarFullResponseDto findCarById(Long id);

    void deleteCar(Long id);
}
