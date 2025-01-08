package app.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.carsharing.dto.rental.CreateRentalRequestDto;
import app.carsharing.dto.rental.RentalFullResponseDto;
import app.carsharing.dto.rental.RentalResponseDto;
import app.carsharing.dto.rental.ReturnRentalRequestDto;
import app.carsharing.exception.EntityNotFoundException;
import app.carsharing.exception.RentalException;
import app.carsharing.mapper.RentalMapper;
import app.carsharing.model.Rental;
import app.carsharing.model.User;
import app.carsharing.model.car.Car;
import app.carsharing.repository.car.CarRepository;
import app.carsharing.repository.rental.RentalRepository;
import app.carsharing.repository.rental.RentalSpecificationBuilder;
import app.carsharing.repository.user.UserRepository;
import app.carsharing.service.notification.TelegramNotificationService;
import app.carsharing.service.rental.RentalServiceImpl;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RentalMapper rentalMapper;

    @Mock
    private CarRepository carRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private RentalSpecificationBuilder specificationBuilder;

    @Mock
    private TelegramNotificationService notificationService;

    @InjectMocks
    private RentalServiceImpl rentalService;

    @Test
    void createRental_ShouldCreateRental_WhenCarIsAvailable() {
        // Arrange
        Long userId = 1L;
        CreateRentalRequestDto requestDto = new CreateRentalRequestDto();
        requestDto.setCarId(1L);
        Car car = new Car();
        car.setInventory(1);
        Rental rental = new Rental();
        User user = new User();
        RentalFullResponseDto responseDto = new RentalFullResponseDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(carRepository.findById(requestDto.getCarId())).thenReturn(Optional.of(car));
        when(rentalMapper.toRental(requestDto)).thenReturn(rental);
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toRentalFullResponseDto(rental)).thenReturn(responseDto);

        // Act
        RentalFullResponseDto result = rentalService.createRental(requestDto, userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(carRepository).save(car);
        verify(notificationService, never()).sendNotification(any(), any());
    }

    @Test
    void createRental_ShouldThrowException_WhenCarIsUnavailable() {
        // Arrange
        Long userId = 1L;
        CreateRentalRequestDto requestDto = new CreateRentalRequestDto();
        requestDto.setCarId(1L);
        Car car = new Car();
        car.setInventory(0);

        User user = new User();
        Rental rental = new Rental(); // Створюємо об'єкт Rental

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(carRepository.findById(requestDto.getCarId())).thenReturn(Optional.of(car));
        when(rentalMapper.toRental(requestDto)).thenReturn(rental); // Мок для mapper

        // Act & Assert
        RentalException exception = assertThrows(RentalException.class,
                () -> rentalService.createRental(requestDto, userId));

        // Assert
        assertNotNull(exception);
        assertEquals("No cars available anymore. Please, choose another car.",
                exception.getMessage());
        verify(carRepository, times(0)).save(any(Car.class));
        verify(rentalRepository, times(0)).save(any(Rental.class));
    }

    @Test
    void findRentalById_ShouldReturnRental_WhenRentalExists() {
        // Arrange
        Long rentalId = 1L;
        Long userId = 1L;
        Rental rental = new Rental();
        RentalFullResponseDto responseDto = new RentalFullResponseDto();

        when(rentalRepository.findByIdAndUserId(rentalId, userId))
                .thenReturn(Optional.of(rental));
        when(rentalMapper.toRentalFullResponseDto(rental)).thenReturn(responseDto);

        // Act
        RentalFullResponseDto result = rentalService.findRentalById(rentalId, userId);

        // Assert
        assertNotNull(result);
    }

    @Test
    void findRentalById_ShouldThrowException_WhenRentalDoesNotExist() {
        // Arrange
        Long rentalId = 1L;
        Long userId = 1L;

        when(rentalRepository.findByIdAndUserId(rentalId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> rentalService.findRentalById(rentalId, userId));
        assertTrue(exception.getMessage().contains("Can't find rental with id: " + rentalId));
    }

    @Test
    void returnRental_ShouldUpdateRental_WhenRentalExists() {
        // Arrange
        Long rentalId = 1L;
        Long userId = 1L;
        Rental rental = new Rental();
        ReturnRentalRequestDto requestDto = new ReturnRentalRequestDto();
        requestDto.setActualDate(LocalDate.parse("2025-01-08"));
        RentalResponseDto responseDto = new RentalResponseDto();

        when(rentalRepository.findByIdAndUserId(rentalId, userId))
                .thenReturn(Optional.of(rental));
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(rentalMapper.toRentalResponseDto(rental)).thenReturn(responseDto);

        // Act
        RentalResponseDto result = rentalService.returnRental(rentalId, requestDto, userId);

        // Assert
        assertNotNull(result);
        assertEquals(requestDto.getActualDate(), result.getActualReturnDate());
    }
}
