package app.carsharing.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.carsharing.dto.car.CarFullResponseDto;
import app.carsharing.dto.car.CarShortResponseDto;
import app.carsharing.dto.rental.CreateRentalRequestDto;
import app.carsharing.dto.rental.RentalFullResponseDto;
import app.carsharing.dto.rental.RentalResponseDto;
import app.carsharing.dto.rental.ReturnRentalRequestDto;
import app.carsharing.dto.user.UserResponseDto;
import app.carsharing.model.Rental;
import app.carsharing.model.Role;
import app.carsharing.model.User;
import app.carsharing.model.car.Car;
import app.carsharing.util.TestUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@Sql(scripts = {
        "classpath:database/car/add-cars-to-cars-table.sql",
        "classpath:database/user/add-users-to-users-table.sql",
        "classpath:database/user/add-users-roles.sql",
        "classpath:database/rental/add-rentals-to-rentals-table.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/rental/remove-rentals.sql",
        "classpath:database/user/remove-users-roles.sql",
        "classpath:database/car/remove-cars-from-cars-table.sql",
        "classpath:database/user/remove-users.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RentalControllerTest {
    protected static MockMvc mockMvc;
    private static final Role USER_ROLE = new Role().setRole(Role.RoleName.ROLE_CUSTOMER);
    private static final User USER = TestUtil.getUser(USER_ROLE);
    private static final UserResponseDto USER_RESPONSE_DTO = TestUtil.userResponseDto(USER);
    private static final Car CAR = TestUtil.getFirstCar();
    private static final CarFullResponseDto CAR_RESPONSE_DTO =
            TestUtil.getFirstCarFullResponseDto();
    private static final Rental ACTIVE_RENTAL = TestUtil.getActiveRental(USER, CAR);
    private static final RentalFullResponseDto ACTIVE_RENTAL_RESPONSE_DTO =
            TestUtil.rentalFullResponseDto(ACTIVE_RENTAL,CAR_RESPONSE_DTO, USER_RESPONSE_DTO);
    private static final RentalResponseDto ACTIVE_RENTAL_SHORT_RESPONSE_DTO =
            TestUtil.rentalShortResponseDto(ACTIVE_RENTAL, CAR, USER);
    private static final CreateRentalRequestDto RENTAL_REQUEST_DTO =
            new CreateRentalRequestDto()
                    .setCarId(1L)
                    .setRentalDate(ACTIVE_RENTAL.getRentalDate())
                    .setReturnDate(ACTIVE_RENTAL.getReturnDate());
    private static final ReturnRentalRequestDto RETURN_DATE_DTO =
            new ReturnRentalRequestDto()
                    .setActualDate(LocalDate.of(2024, 8, 1));
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithUserDetails("john.doe@example.com")
    @Test
    @DisplayName("Create a new rental")
    public void createRental_ValidRequest_WillReturnRentalDto() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(RENTAL_REQUEST_DTO);

        MvcResult result = mockMvc.perform(
                        post("/rentals")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        RentalFullResponseDto actualResponseDto = objectMapper
                .readValue(result.getResponse().getContentAsString(),
                        RentalFullResponseDto.class);

        Assertions.assertNotNull(actualResponseDto);
        Assertions.assertNotNull(actualResponseDto.getId());
        EqualsBuilder.reflectionEquals(ACTIVE_RENTAL_RESPONSE_DTO, actualResponseDto, "id");
    }

    @WithUserDetails("john.doe@example.com")
    @Test
    @DisplayName("Get all rentals by active status")
    public void getAllRentalsByActiveStatus_ShouldReturnListOfRentals() throws Exception {
        List<RentalResponseDto> expectedList = Arrays.asList(ACTIVE_RENTAL_SHORT_RESPONSE_DTO);

        MvcResult result = mockMvc.perform(
                        get("/rentals")
                                .param("isActive", "true")
                                .param("userId", "2")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode contentNode = jsonNode.get("content");
        CarShortResponseDto[] actual =
                objectMapper.treeToValue(contentNode, CarShortResponseDto[].class);
        Assertions.assertEquals(expectedList.size(), actual.length);
    }

    @WithUserDetails("john.doe@example.com")
    @Test
    @DisplayName("Get rental by id")
    public void getRentalById_ValidId_ShouldReturnRentalDto() throws Exception {
        Long id = 1L;

        MvcResult result = mockMvc.perform(
                        get("/rentals/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalFullResponseDto actualResponseDto = objectMapper
                .readValue(result.getResponse().getContentAsString(),
                        RentalFullResponseDto.class);
        Assertions.assertNotNull(actualResponseDto);
        Assertions.assertEquals(ACTIVE_RENTAL_RESPONSE_DTO.getId(), actualResponseDto.getId());
    }

    @WithUserDetails("john.doe@example.com")
    @Test
    @DisplayName("Return rental by id")
    public void returnRental_ValidId_ShouldReturnRentalDto() throws Exception {
        Long id = 1L;

        String jsonRequest = objectMapper.writeValueAsString(RETURN_DATE_DTO);

        MvcResult result = mockMvc.perform(
                        post("/rentals/{id}/return", id)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalResponseDto actualResponseDto = objectMapper
                .readValue(result.getResponse().getContentAsString(), RentalResponseDto.class);
        Assertions.assertNotNull(actualResponseDto);
        Assertions.assertEquals(ACTIVE_RENTAL_SHORT_RESPONSE_DTO.getId(),
                actualResponseDto.getId());
    }
}
