package app.carsharing.mapper;

import app.carsharing.config.MapperConfig;
import app.carsharing.dto.payment.PaymentFullResponseDto;
import app.carsharing.dto.payment.PaymentShortResponseDto;
import app.carsharing.dto.payment.PaymentStatusResponseDto;
import app.carsharing.model.payment.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(target = "sessionUrl", source = "session")
    PaymentShortResponseDto toShortResponseDto(Payment payment);

    @Mapping(target = "rentalId", source = "rental.id")
    @Mapping(target = "sessionUrl", source = "session")
    PaymentFullResponseDto toFullResponseDto(Payment payment);

    PaymentStatusResponseDto toStatusResponseDto(Payment payment);
}
