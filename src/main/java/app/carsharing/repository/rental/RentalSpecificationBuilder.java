package app.carsharing.repository.rental;

import app.carsharing.dto.rental.RentalSearchParametersDto;
import app.carsharing.model.Rental;
import app.carsharing.repository.SpecificationBuilder;
import app.carsharing.repository.rental.spec.IsActiveSpecificationProvider;
import app.carsharing.repository.rental.spec.UserIdSpecificationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentalSpecificationBuilder implements SpecificationBuilder<Rental> {
    private final RentalSpecificationProviderManager providerManager;

    @Override
    public Specification<Rental> build(RentalSearchParametersDto rentalSearchParameters) {
        Specification<Rental> specification = Specification.where(null);
        if (rentalSearchParameters.getUserId() != null) {
            specification = specification.and(providerManager
                    .getSpecificationProvider(UserIdSpecificationProvider.USER)
                    .getSpecification(rentalSearchParameters.getUserId())
            );
        }
        if (rentalSearchParameters.getIsActive() != null) {
            specification = specification.and(providerManager
                    .getSpecificationProvider(IsActiveSpecificationProvider.IS_ACTIVE)
                    .getSpecification(rentalSearchParameters.getIsActive()));
        }
        return specification;
    }
}
