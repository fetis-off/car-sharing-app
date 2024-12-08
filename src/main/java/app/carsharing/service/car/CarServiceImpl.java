package app.carsharing.service.car;

import app.carsharing.dto.car.CarFullResponseDto;
import app.carsharing.dto.car.CarShortResponseDto;
import app.carsharing.dto.car.CreateCarRequestDto;
import app.carsharing.dto.car.UpdateCarInventoryDto;
import app.carsharing.exception.EntityNotFoundException;
import app.carsharing.mapper.CarMapper;
import app.carsharing.model.car.Car;
import app.carsharing.model.car.CarType;
import app.carsharing.repository.car.CarRepository;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarFullResponseDto createCar(CreateCarRequestDto requestDto) {
        if (!isValidCarType((requestDto.getCarType()))) {
            throw new IllegalArgumentException("Invalid car type: "
                    + requestDto.getCarType()
                    + ". Allowed values: "
                    + Arrays.toString(CarType.values()));
        }
        Car car = carMapper.toCar(requestDto);
        return carMapper.toCarFullResponseDto(carRepository.save(car));
    }

    @Override
    public Page<CarFullResponseDto> findAllCarsFull(Pageable pageable) {
        return carRepository.findAll(pageable)
                .map(carMapper::toCarFullResponseDto);
    }

    @Override
    public Page<CarShortResponseDto> findAllCarsBrief(Pageable pageable) {
        return carRepository.findAll(pageable)
                .map(carMapper::toCarShortResponseDto);
    }

    @Override
    public CarFullResponseDto updateCar(Long id, CreateCarRequestDto requestDto) {
        Car existedCar = findCarById(id);
        carMapper.updateCar(requestDto, existedCar);
        return carMapper.toCarFullResponseDto(carRepository.save(existedCar));
    }

    @Override
    public CarFullResponseDto updateCarInventory(Long id, UpdateCarInventoryDto requestDto) {
        Car existedCar = findCarById(id);
        existedCar.setInventory(requestDto.getInventory());
        return carMapper.toCarFullResponseDto(carRepository.save(existedCar));
    }

    @Override
    public void deleteCar(Long id) {
        Car existedCar = findCarById(id);
        carRepository.delete(existedCar);
    }

    private boolean isValidCarType(String carType) {
        return Arrays.stream(CarType.values())
                .anyMatch(t -> t.name().equalsIgnoreCase(carType));
    }

    private Car findCarById(Long id) {
        return carRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Car with id: "
                        + id + " not found"));
    }
}
