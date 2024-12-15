package app.carsharing.service.rental;

import app.carsharing.dto.rental.CreateRentalRequestDto;
import app.carsharing.dto.rental.RentalFullResponseDto;
import app.carsharing.dto.rental.RentalResponseDto;
import app.carsharing.dto.rental.RentalSearchParametersDto;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final UserRepository userRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final RentalRepository rentalRepository;
    private final RentalSpecificationBuilder specificationBuilder;

    @Transactional
    @Override
    public RentalFullResponseDto createRental(CreateRentalRequestDto requestDto, Long userId) {
        Rental rental = rentalMapper.toRental(requestDto);
        User user = findUserById(userId);
        rental.setUser(user);
        Car car = findCarById(requestDto.getCarId());
        if (car.getInventory() < 1) {
            throw new RentalException("No cars available anymore. Please, choose another car.");
        }
        car.setInventory(car.getInventory() - 1);
        rental.setCar(car);
        RentalFullResponseDto responseDto = rentalMapper
                .toRentalFullResponseDto(rentalRepository.save(rental));
        responseDto.setUserId(userId);

        return responseDto;
    }

    @Override
    public RentalFullResponseDto findRentalById(Long rentalId, Long userId) {
        Rental rental = rentalRepository.findByIdAndUserId(rentalId, userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find rental with id: "
                        + rentalId
                        + " and user with id: "
                        + userId));
        RentalFullResponseDto responseDto = rentalMapper.toRentalFullResponseDto(rental);
        responseDto.setUserId(userId);
        return responseDto;
    }

    @Override
    public Page<RentalResponseDto> search(RentalSearchParametersDto parameters,
                                          Pageable pageable) {
        Specification<Rental> specification = specificationBuilder.build(parameters);

        return rentalRepository.findAll(specification, pageable)
                .map(rentalMapper::toRentalResponseDto);
    }

    @Override
    public RentalResponseDto returnRental(Long rentalId,
                                          ReturnRentalRequestDto requestDto,
                                          Long userId) {
        Rental rental = rentalRepository.findByIdAndUserId(rentalId, userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find rental with id: "
                        + rentalId
                        + " and user with id: "
                        + userId));
        rentalMapper.setActualReturnDate(requestDto, rental);

        RentalResponseDto responseDto = rentalMapper
                .toRentalResponseDto(rentalRepository.save(rental));

        responseDto.setActualReturnDate(requestDto.getActualDate());
        return responseDto;
    }

    private Car findCarById(Long carId) {
        return carRepository.findById(carId).orElseThrow(
                () -> new EntityNotFoundException("Car with id "
                        + carId
                        + " not found")
        );
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Car with id "
                        + userId
                        + " not found")
        );
    }
}
