package app.carsharing.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRoleRequestDto {
    @NotBlank(message = "Role name can't be empty")
    private String role;
}
