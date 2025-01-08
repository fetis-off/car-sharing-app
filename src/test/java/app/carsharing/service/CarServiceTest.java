package app.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.carsharing.dto.car.CarFullResponseDto;
import app.carsharing.dto.car.CreateCarRequestDto;
import app.carsharing.dto.car.UpdateCarInventoryDto;
import app.carsharing.exception.EntityNotFoundException;
import app.carsharing.mapper.CarMapper;
import app.carsharing.model.car.Car;
import app.carsharing.repository.car.CarRepository;
import app.carsharing.service.car.CarServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {
    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    void createCar_ShouldCreateCar_WhenCarTypeIsValid() {
        // Arrange
        CreateCarRequestDto requestDto = new CreateCarRequestDto();
        requestDto.setCarType("SUV");
        Car car = new Car();
        CarFullResponseDto responseDto = new CarFullResponseDto();

        when(carMapper.toCar(requestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toCarFullResponseDto(car)).thenReturn(responseDto);

        // Act
        CarFullResponseDto result = carService.createCar(requestDto);

        // Assert
        assertNotNull(result);
        verify(carRepository).save(car);
    }

    @Test
    void createCar_ShouldThrowException_WhenCarTypeIsInvalid() {
        // Arrange
        CreateCarRequestDto requestDto = new CreateCarRequestDto();
        requestDto.setCarType("INVALID_TYPE");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> carService.createCar(requestDto));
        assertTrue(exception.getMessage().contains("Invalid car type"));
    }

    @Test
    void findAllCarsFull_ShouldReturnPageOfCars() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);
        Car car = new Car();
        CarFullResponseDto responseDto = new CarFullResponseDto();

        when(carRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(car)));
        when(carMapper.toCarFullResponseDto(car)).thenReturn(responseDto);

        // Act
        Page<CarFullResponseDto> result = carService.findAllCarsFull(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findCarById_ShouldReturnCar_WhenCarExists() {
        // Arrange
        Long carId = 1L;
        Car car = new Car();
        CarFullResponseDto responseDto = new CarFullResponseDto();

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(carMapper.toCarFullResponseDto(car)).thenReturn(responseDto);

        // Act
        CarFullResponseDto result = carService.findCarById(carId);

        // Assert
        assertNotNull(result);
    }

    @Test
    void findCarById_ShouldThrowException_WhenCarDoesNotExist() {
        // Arrange
        Long carId = 1L;

        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> carService.findCarById(carId));
        assertTrue(exception.getMessage().contains("Car with id: " + carId + " not found"));
    }

    @Test
    void deleteCar_ShouldDeleteCar_WhenCarExists() {
        // Arrange
        Long carId = 1L;
        Car car = new Car();

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));

        // Act
        carService.deleteCar(carId);

        // Assert
        verify(carRepository).delete(car);
    }

    @Test
    void updateCarInventory_ShouldUpdateInventory_WhenCarExists() {
        // Arrange
        Long carId = 1L;
        Car car = new Car();
        UpdateCarInventoryDto requestDto = new UpdateCarInventoryDto();
        requestDto.setInventory(10);
        CarFullResponseDto responseDto = new CarFullResponseDto();

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toCarFullResponseDto(car)).thenReturn(responseDto);

        // Act
        CarFullResponseDto result = carService.updateCarInventory(carId, requestDto);

        // Assert
        assertNotNull(result);
        assertEquals(10, car.getInventory());
    }
}
