package app.carsharing.config;

import com.stripe.Stripe;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    private final String apiKey;

    public StripeConfig() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("STRIPE_API_KEY");
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }
}
