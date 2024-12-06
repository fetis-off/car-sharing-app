package app.carsharing.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdateUserProfileRequestDto {
    @NotBlank(message = "Firstname can`t be empty")
    @Length(max = 60, message = "Firstname can't be longer than 60 symbols")
    private String firstName;

    @NotBlank(message = "Lastname can`t be empty")
    @Length(max = 60, message = "Lastname can't be longer than 60 symbols")
    private String lastName;

    @NotBlank(message = "Email can`t be empty")
    @Email
    private String email;
}
