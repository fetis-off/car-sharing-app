package app.carsharing.service.user;

import app.carsharing.dto.user.UpdateUserProfileRequestDto;
import app.carsharing.dto.user.UpdateUserRoleRequestDto;
import app.carsharing.dto.user.UserRegistrationRequestDto;
import app.carsharing.dto.user.UserResponseDto;
import app.carsharing.exception.EntityNotFoundException;
import app.carsharing.exception.RegistrationException;
import app.carsharing.mapper.UserMapper;
import app.carsharing.model.Role;
import app.carsharing.model.User;
import app.carsharing.repository.role.RoleRepository;
import app.carsharing.repository.user.UserRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException(
                    String.format("User with this email: %s already exists",
                    requestDto.getEmail()));
        }
        User user = userMapper.toUser(requestDto);
        user.setRoles(Set.of(roleRepository.findByRole(Role.RoleName.ROLE_CUSTOMER)));
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(user);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto updateRole(Long id, UpdateUserRoleRequestDto requestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Couldn't find user with id: " + id)
                );
        Set<Role> roles = Set.of(roleRepository
                .findByRole(Role.RoleName.valueOf(requestDto.getRole())));
        user.setRoles(roles);
        return userMapper.toUserResponseDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updateUserProfile(UpdateUserProfileRequestDto requestDto,
                                             Long userId) {
        User existedUser = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Couldn't find user with id: "
                        + userId)
        );
        userMapper.updateUserFromDto(requestDto, existedUser);
        return userMapper.toUserResponseDto(userRepository.save(existedUser));
    }

    @Override
    public List<UserResponseDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).stream()
                .map(userMapper::toUserResponseDto)
                .toList();
    }
}
