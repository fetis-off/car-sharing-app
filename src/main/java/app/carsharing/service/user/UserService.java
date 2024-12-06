package app.carsharing.service.user;

import app.carsharing.dto.user.UpdateUserProfileRequestDto;
import app.carsharing.dto.user.UpdateUserRoleRequestDto;
import app.carsharing.dto.user.UserRegistrationRequestDto;
import app.carsharing.dto.user.UserResponseDto;
import app.carsharing.exception.RegistrationException;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException;

    UserResponseDto updateRole(Long id, UpdateUserRoleRequestDto requestDto);

    UserResponseDto updateUserProfile(UpdateUserProfileRequestDto requestDto, Long userId);

    List<UserResponseDto> findAll(Pageable pageable);
}
