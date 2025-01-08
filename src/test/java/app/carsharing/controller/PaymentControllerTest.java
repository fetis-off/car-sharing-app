package app.carsharing.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.carsharing.dto.payment.PaymentFullResponseDto;
import app.carsharing.dto.payment.PaymentStatusResponseDto;
import app.carsharing.model.Rental;
import app.carsharing.model.Role;
import app.carsharing.model.User;
import app.carsharing.model.payment.Payment;
import app.carsharing.model.payment.PaymentStatus;
import app.carsharing.util.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.github.dockerjava.core.MediaType;

@Sql(scripts = {
        "classpath:database/user/add-users-to-users-table.sql",
        "classpath:database/user/add-users-roles.sql",
        "classpath:database/car/add-cars-to-cars-table.sql",
        "classpath:database/rental/add-rentals-to-rentals-table.sql",
        "classpath:database/payment/add-payment-to-payments-table.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/payment/remove-payments.sql",
        "classpath:database/rental/remove-rentals.sql",
        "classpath:database/user/remove-users-roles.sql",
        "classpath:database/user/remove-users.sql",
        "classpath:database/car/remove-cars-from-cars-table.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerTest {
    protected static MockMvc mockMvc;
    private static final String PAYMENT_CANCELED_MESSAGE = "Payment session canceled";
    private static final Role USER_ROLE = new Role().setRole(Role.RoleName.ROLE_CUSTOMER);
    private static final User USER = TestUtil.getUser(USER_ROLE);
    private static final Rental RENTAL = TestUtil.getRental(USER);
    private static final Payment PAYMENT = TestUtil.getPayment(RENTAL);
    private static final PaymentFullResponseDto PAYMENT_FULL_RESPONSE_DTO =
            TestUtil.paymentFullResponseDto(PAYMENT);
    private static final PaymentStatusResponseDto PAYMENT_STATUS_RESPONSE_DTO =
            new PaymentStatusResponseDto()
                    .setStatus(PaymentStatus.CANCELLED)
                    .setSessionId(PAYMENT.getSessionId())
                    .setMessage(PAYMENT_CANCELED_MESSAGE);

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Handle cancel payment with valid session id")
    void handleCancel_withValidSessionId_ShouldReturnCancelMessage() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/payments/cancel/{sessionId}", "sessionId")
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andReturn();
        PaymentStatusResponseDto actualPayment =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        PaymentStatusResponseDto.class);
        Assertions.assertNotNull(actualPayment);
        assertThat(PAYMENT_STATUS_RESPONSE_DTO).usingRecursiveComparison()
                .ignoringFields("id").isEqualTo(actualPayment);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Handle cancel payment with invalid session id should throw exception")
    void handleCancel_withInvalidSessionId_ShouldThrowPaymentException() throws Exception {
        mockMvc.perform(
                        get("/payments/cancel/{sessionId}", "someId")
                                .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isNotFound());
    }
}





