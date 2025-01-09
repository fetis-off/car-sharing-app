package app.carsharing.service.notification;

import app.carsharing.model.Rental;
import app.carsharing.model.User;
import app.carsharing.repository.rental.RentalRepository;
import app.carsharing.repository.user.UserRepository;
import app.carsharing.utils.Message;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalOverdueCheckerService {
    private final TelegramNotificationService notificationService;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    private void checkOverDueRentals() {
        List<Rental> overdueRentals = rentalRepository.findOverdueRentals(LocalDate.now());
        Map<Long, List<Rental>> rentalsByUser = groupRentalsByUser(overdueRentals);
        notifyAllUsers(rentalsByUser);
    }

    private Map<Long, List<Rental>> groupRentalsByUser(List<Rental> overdueRentals) {
        return overdueRentals.stream()
                .collect(Collectors.groupingBy(rental -> rental.getUser().getId()));
    }

    private void notifyAllUsers(Map<Long, List<Rental>> rentalsByUser) {
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            Long userId = user.getId();
            Long tgChatId = user.getTgChatId();

            if (tgChatId != null) {
                notifyUser(user, rentalsByUser.get(userId));
            }
        }
    }

    private void notifyUser(User user, List<Rental> userOverdueRentals) {
        Long tgChatId = user.getTgChatId();
        if (userOverdueRentals != null && !userOverdueRentals.isEmpty()) {
            for (Rental rental : userOverdueRentals) {
                notificationService.sendNotification(
                        tgChatId,
                        Message.getOverdueRentalMessageForTg(rental)
                );
            }
        } else {
            notificationService.sendNotification(
                    tgChatId,
                    "You have no overdue rentals. Thank you for returning your rentals on time!"
            );
        }
    }
}

