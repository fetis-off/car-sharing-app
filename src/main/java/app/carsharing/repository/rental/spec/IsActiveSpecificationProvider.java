package app.carsharing.repository.rental.spec;

import app.carsharing.model.Rental;
import app.carsharing.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsActiveSpecificationProvider implements SpecificationProvider<Rental> {
    public static final String IS_ACTIVE = "isActive";
    public static final String DATE_FIELD = "actualDate";

    @Override
    public String getKey() {
        return IS_ACTIVE;
    }

    public Specification<Rental> getSpecification(String param) {
        return (root, query, criteriaBuilder) -> {
            if (param.equals("true")) {
                return criteriaBuilder.isNull(root.get(DATE_FIELD));
            } else if (param.equals("false")) {
                return criteriaBuilder.isNotNull(root.get(DATE_FIELD));
            } else {
                throw new IllegalArgumentException(
                        "Param should contains true or false, your param: " + param);
            }
        };
    }
}
