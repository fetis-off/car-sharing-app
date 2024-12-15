package app.carsharing.controller;

import app.carsharing.dto.rental.CreateRentalRequestDto;
import app.carsharing.dto.rental.RentalFullResponseDto;
import app.carsharing.dto.rental.RentalResponseDto;
import app.carsharing.dto.rental.RentalSearchParametersDto;
import app.carsharing.dto.rental.ReturnRentalRequestDto;
import app.carsharing.model.Role;
import app.carsharing.model.User;
import app.carsharing.service.rental.RentalServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
@Tag(name = "Rental controller", description = "Endpoints for operations with rentals")
public class RentalController {
    private final RentalServiceImpl rentalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new rental")
    public RentalFullResponseDto createRental(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CreateRentalRequestDto request) {
        return rentalService.createRental(request, user.getId());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Create a new rental")
    public RentalFullResponseDto getRentalById(@PathVariable Long id,
                                           @AuthenticationPrincipal User user) {
        return rentalService.findRentalById(id, user.getId());
    }

    @GetMapping
    @Operation(summary = "Get page of rentals",
            description = "Allows the customer to find all his rentals filtered by isActive"
                    + "and allows the manager to find all rentals for any/all users")
    public Page<RentalResponseDto> findAllByActiveStatus(
            @AuthenticationPrincipal User user,
            String userId,
            @NotBlank String isActive,
            @PageableDefault Pageable pageable) {

        RentalSearchParametersDto params = new RentalSearchParametersDto();
        params.setUserId(userId);
        params.setIsActive(isActive);

        boolean isManager = user.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals(Role.RoleName.ROLE_ADMIN.name()));

        if (!isManager && (userId == null
                || !userId.equals(user.getId().toString()))) {
            throw new AccessDeniedException("You are not allowed to view rentals of another user");
        }

        if (isManager && userId == null) {
            params.setUserId(user.getId().toString());
        }

        return rentalService.search(params, pageable);
    }

    @PostMapping("/{id}/return")
    @Operation(summary = "Return rental",
            description = "Allows the customer to return a rental")
    public RentalResponseDto returnRental(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ReturnRentalRequestDto requestDto) {
        return rentalService.returnRental(id, requestDto, user.getId());
    }
}
