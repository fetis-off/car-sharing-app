package app.carsharing.controller;

import app.carsharing.dto.user.UpdateTgChatIdRequestDto;
import app.carsharing.dto.user.UpdateUserProfileRequestDto;
import app.carsharing.dto.user.UpdateUserRoleRequestDto;
import app.carsharing.dto.user.UserResponseDto;
import app.carsharing.model.User;
import app.carsharing.service.user.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User profile controller", description = "Endpoints for managing profile operations")
public class UserController {
    private final UserServiceImpl userService;

    @Operation(summary = "Update user role", description = "Only for managers")
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/role")
    public UserResponseDto updateRole(@PathVariable Long id,
                                      @Valid @RequestBody UpdateUserRoleRequestDto requestDto) {
        return userService.updateRole(id, requestDto);
    }

    @Operation(summary = "Update user profile", description = "Only for managers")
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/me")
    public UserResponseDto updateProfile(@Valid @RequestBody UpdateUserProfileRequestDto requestDto,
                                         @AuthenticationPrincipal User user) {
        return userService.updateUserProfile(requestDto, user.getId());
    }

    @Operation(summary = "Get all user profiles")
    @GetMapping("/me")
    public Page<UserResponseDto> getAllUsersProfile(@PageableDefault(size = 20) Pageable pageable) {
        return userService.findAll(pageable);
    }

    @PatchMapping("/{id}/telegram")
    @Operation(summary = "Update user telegram chatId", description = "Allowed to all")
    public UserResponseDto updateTgChatId(
            @PathVariable @Positive Long id,
            @RequestBody @Valid UpdateTgChatIdRequestDto requestDto) {
        return userService.updateTgChatId(id, requestDto);
    }
}
