package app.carsharing.dto.user;

import app.carsharing.validation.user.Email;
import app.carsharing.validation.user.FieldMatch;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@FieldMatch(first = "password", second = "repeatPassword")
public class UserRegistrationRequestDto {
    @NotBlank(message = "Firstname can`t be empty")
    private String firstName;

    @NotBlank(message = "Lastname can`t be empty")
    private String lastName;

    @NotBlank(message = "Email can`t be empty")
    @Email
    private String email;

    @NotBlank(message = "Password can`t be empty")
    @Length(min = 8, max = 40)
    private String password;

    @NotBlank
    @Length(min = 8, max = 40)
    private String repeatPassword;
}
