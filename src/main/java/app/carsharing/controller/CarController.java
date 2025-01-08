package app.carsharing.controller;

import app.carsharing.dto.car.CarFullResponseDto;
import app.carsharing.dto.car.CarShortResponseDto;
import app.carsharing.dto.car.CreateCarRequestDto;
import app.carsharing.dto.car.UpdateCarInventoryDto;
import app.carsharing.service.car.CarServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
@Tag(name = "Car controller", description = "Endpoints for managing car operations")
public class CarController {
    private final CarServiceImpl carService;

    @Operation(summary = "Create a new car", description = "Only for managers")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CarFullResponseDto createCar(@Valid @RequestBody CreateCarRequestDto requestDto) {
        return carService.createCar(requestDto);
    }

    @Operation(summary = "Get all cars short information")
    @GetMapping
    public Page<CarFullResponseDto> getAllCarsFullInf(
            @PageableDefault(size = 20) Pageable pageable) {
        return carService.findAllCarsFull(pageable);
    }

    @Operation(summary = "Get all cars detailed information")
    @GetMapping("/")
    public Page<CarShortResponseDto> getAllCarsShortInf(
            @PageableDefault(size = 20) Pageable pageable) {
        return carService.findAllCarsBrief(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update all car details", description = "Only for managers")
    @PutMapping("/{id}")
    public CarFullResponseDto updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CreateCarRequestDto requestDto) {
        return carService.updateCar(id, requestDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a car by id", description = "Returns a full info about car by id")
    public CarFullResponseDto getCarById(@PathVariable @Positive Long id) {
        return carService.findCarById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update car inventory", description = "Only for managers")
    @PatchMapping("/{id}")
    public CarFullResponseDto updateCarInventory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCarInventoryDto requestDto) {
        return carService.updateCarInventory(id, requestDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete car by id", description = "Only for managers")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCarInventory(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}
