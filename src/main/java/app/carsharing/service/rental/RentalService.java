package app.carsharing.service.rental;

import app.carsharing.dto.rental.CreateRentalRequestDto;
import app.carsharing.dto.rental.RentalFullResponseDto;
import app.carsharing.dto.rental.RentalResponseDto;
import app.carsharing.dto.rental.RentalSearchParametersDto;
import app.carsharing.dto.rental.ReturnRentalRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalFullResponseDto createRental(CreateRentalRequestDto requestDto, Long userId);

    RentalFullResponseDto findRentalById(Long rentalId, Long userId);

    Page<RentalResponseDto> search(RentalSearchParametersDto parameters, Pageable pageable);

    RentalResponseDto returnRental(Long rentalId, ReturnRentalRequestDto requestDto, Long userId);
}
