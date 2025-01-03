package app.carsharing.repository.role;

import app.carsharing.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRole(Role.RoleName roleName);
}
