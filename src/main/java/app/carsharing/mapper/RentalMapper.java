package app.carsharing.mapper;

import app.carsharing.config.MapperConfig;
import app.carsharing.dto.rental.CreateRentalRequestDto;
import app.carsharing.dto.rental.RentalFullResponseDto;
import app.carsharing.dto.rental.RentalResponseDto;
import app.carsharing.dto.rental.ReturnRentalRequestDto;
import app.carsharing.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    Rental toRental(CreateRentalRequestDto requestDto);

    RentalFullResponseDto toRentalFullResponseDto(Rental rental);

    @Mapping(target = "carId", source = "car.id")
    @Mapping(target = "userId", source = "user.id")
    RentalResponseDto toRentalResponseDto(Rental rental);

    void setActualReturnDate(ReturnRentalRequestDto requestDto, @MappingTarget Rental rental);
}
