package app.carsharing.util;

import app.carsharing.dto.car.CarFullResponseDto;
import app.carsharing.dto.car.CarShortResponseDto;
import app.carsharing.dto.car.CreateCarRequestDto;
import app.carsharing.dto.payment.PaymentFullResponseDto;
import app.carsharing.dto.rental.RentalFullResponseDto;
import app.carsharing.dto.rental.RentalResponseDto;
import app.carsharing.dto.user.UserResponseDto;
import app.carsharing.model.Rental;
import app.carsharing.model.Role;
import app.carsharing.model.User;
import app.carsharing.model.car.Car;
import app.carsharing.model.car.CarType;
import app.carsharing.model.payment.Payment;
import app.carsharing.model.payment.PaymentStatus;
import app.carsharing.model.payment.PaymentType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public final class TestUtil {
    private TestUtil() {
    }

    public static CarFullResponseDto getSecondCarFullResponseDto() {
        return new CarFullResponseDto()
                .setId(2L)
                .setModel("Accord")
                .setBrand("Honda")
                .setCarType("SEDAN")
                .setInventory(10)
                .setDailyFee(BigDecimal.valueOf(80).setScale(2));
    }

    public static CarFullResponseDto getFirstCarFullResponseDto() {
        return new CarFullResponseDto()
                .setId(1L)
                .setModel("Octavia")
                .setBrand("Skoda")
                .setCarType("HATCHBACK")
                .setInventory(10)
                .setDailyFee(BigDecimal.valueOf(65).setScale(2));
    }

    public static CarShortResponseDto getFirstCarShortResponseDto() {
        return new CarShortResponseDto()
                .setId(1L)
                .setModel("Octavia")
                .setCarType("HATCHBACK");
    }

    public static CarShortResponseDto getSecondCarShortResponseDto() {
        return new CarShortResponseDto()
                .setId(2L)
                .setModel("Accord")
                .setCarType("SEDAN");
    }

    public static Car getFirstCar() {
        return new Car()
                .setId(1L)
                .setModel("Octavia")
                .setBrand("Skoda")
                .setCarType(CarType.valueOf("HATCHBACK"))
                .setInventory(10)
                .setDailyFee(BigDecimal.valueOf(65).setScale(2));
    }

    public static CreateCarRequestDto createFirstCarRequestDto() {
        CarFullResponseDto responseDto = getFirstCarFullResponseDto();
        return new CreateCarRequestDto()
                .setModel(responseDto.getModel())
                .setBrand(responseDto.getBrand())
                .setCarType(responseDto.getCarType())
                .setInventory(responseDto.getInventory())
                .setDailyFee(responseDto.getDailyFee());
    }

    public static CreateCarRequestDto updateCarRequestDto() {
        return new CreateCarRequestDto()
                .setModel("Cayenne")
                .setBrand("Porshe")
                .setCarType("SUV")
                .setInventory(12)
                .setDailyFee(BigDecimal.valueOf(100).setScale(2));
    }

    public static CarFullResponseDto updatedCarResponseDto() {
        CreateCarRequestDto requestDto = updateCarRequestDto();
        return new CarFullResponseDto()
                .setModel(requestDto.getModel())
                .setBrand(requestDto.getBrand())
                .setCarType(requestDto.getCarType())
                .setInventory(requestDto.getInventory())
                .setDailyFee(requestDto.getDailyFee());
    }

    public static User getUser(Role roleName) {
        return new User()
                .setId(1L)
                .setFirstName("John")
                .setLastName("Doe")
                .setEmail("john.doe@example.com")
                .setPassword("password")
                .setRoles(Set.of(roleName));
    }

    public static Rental getRental(User user) {
        return new Rental()
                .setId(1L)
                .setUser(user)
                .setCar(TestUtil.getFirstCar())
                .setRentalDate(LocalDate.of(2024, 9, 10))
                .setReturnDate(LocalDate.of(2024, 9, 12))
                .setDeleted(false);
    }

    public static Payment getPayment(Rental rental) {
        return new Payment()
                .setId(1L)
                .setType(PaymentType.PAYMENT)
                .setStatus(PaymentStatus.PENDING)
                .setRental(rental)
                .setAmountToPay(BigDecimal.valueOf(180))
                .setSessionId("sessionId")
                .setSession("sessionUrl");
    }

    public static PaymentFullResponseDto paymentFullResponseDto(Payment payment) {
        return new PaymentFullResponseDto()
                .setId(payment.getId())
                .setType(payment.getType())
                .setStatus(payment.getStatus())
                .setRentalId(payment.getRental().getId())
                .setSessionId(payment.getSessionId())
                .setSessionUrl(payment.getSession())
                .setAmountToPay(payment.getAmountToPay());
    }

    public static UserResponseDto userResponseDto(User user) {
        return new UserResponseDto()
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail());
    }

    public static Rental getActiveRental(User user, Car car) {
        return new Rental()
                .setId(1L)
                .setUser(user)
                .setCar(car)
                .setRentalDate(LocalDate.of(2024, 7, 20))
                .setReturnDate(LocalDate.of(2024, 7, 29));
    }

    public static RentalFullResponseDto rentalFullResponseDto(Rental rental,
                                                              CarFullResponseDto carDto,
                                                              UserResponseDto userDto) {
        return new RentalFullResponseDto()
                .setId(rental.getId())
                .setCar(carDto)
                .setUserId(userDto.getId())
                .setRentalDate(rental.getRentalDate())
                .setReturnDate(rental.getReturnDate());
    }

    public static RentalResponseDto rentalShortResponseDto(Rental rental,
                                                           Car car,
                                                           User user) {
        return new RentalResponseDto()
                .setId(rental.getId())
                .setCarId(car.getId())
                .setUserId(user.getId())
                .setRentalDate(rental.getRentalDate())
                .setReturnDate(rental.getReturnDate());
    }

}
