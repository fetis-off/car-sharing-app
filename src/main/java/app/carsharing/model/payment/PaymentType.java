package app.carsharing.model.payment;

import lombok.experimental.Accessors;

@Accessors(chain = true)
public enum PaymentType {
    PAYMENT,
    FINE
}
