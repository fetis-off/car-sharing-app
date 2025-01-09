package app.carsharing.utils;

import app.carsharing.model.Rental;
import app.carsharing.model.payment.Payment;

public class Message {

    public static String getRentalMessageForTg(Rental rental) {
        return String.format(
                "🎉 *Congratulations, %s!* 🎉\n\n"
                        + "🚗 You have successfully rented:\n"
                        + "*%s %s*\n\n"
                        + "📅 Rental Period:\n"
                        + "*%tF* ➡️ *%tF*\n\n"
                        + "We wish you a smooth and enjoyable ride! 🏎",
                rental.getUser().getFirstName(),
                rental.getCar().getBrand(),
                rental.getCar().getModel(),
                rental.getRentalDate(),
                rental.getReturnDate()
        );
    }

    public static String getSuccessfulPaymentMessageForTg(Payment payment) {
        return String.format(
                "💳 *Payment Successful!* 💳\n\n"
                        + "✅ Amount Paid: *%.2f USD*\n"
                        + "📅 Valid Until: *%tF*\n\n"
                        + "Thank you for your payment! We hope you enjoy the service. 🌟",
                payment.getAmountToPay(),
                payment.getRental().getReturnDate()
        );
    }

    public static String getOverdueRentalMessageForTg(Rental rental) {
        return String.format(
                "⚠️ *Overdue Rental Alert!* ⚠️\n\n"
                        + "👋 *Hello, %s!*\n\n"
                        + "We hope you enjoyed driving our *%s*. However, your rental "
                        + "period has ended, and the car is now overdue. "
                        + "Here are the details:\n\n"
                        + "🚗 *Car*: %s\n"
                        + "📅 *Rental Period*: %tF ➡️ %tF\n\n"
                        + "⏳ To avoid additional charges, please return the car promptly.\n\n"
                        + "For any questions, feel free to contact our support team. "
                        + "We’re here to help! 😊\n\n"
                        + "Thank you for choosing our service!\n"
                        + "*The Rental Team*",
                rental.getUser().getFirstName(),
                rental.getCar().getBrand() + " " + rental.getCar().getModel(),
                rental.getCar().getBrand() + " " + rental.getCar().getModel(),
                rental.getRentalDate(),
                rental.getReturnDate()
        );
    }
}
