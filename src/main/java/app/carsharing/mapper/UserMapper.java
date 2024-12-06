package app.carsharing.mapper;

import app.carsharing.config.MapperConfig;
import app.carsharing.dto.user.UpdateUserProfileRequestDto;
import app.carsharing.dto.user.UserRegistrationRequestDto;
import app.carsharing.dto.user.UserResponseDto;
import app.carsharing.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponseDto(User user);

    User toUser(UserRegistrationRequestDto requestDto);

    void updateUserFromDto(UpdateUserProfileRequestDto requestDto, @MappingTarget User user);
}
