package io.connektn.stripe.tutorial;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    private final Logger logger = LoggerFactory.getLogger(StripeService.class);

    public StripeService(
            @Value("stripe.secret-key")
            String secretKey
    ) {
        Stripe.apiKey = secretKey;
    }

    @Async
    public void processEventAsync(Event event) {
        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(event);
                break;
            case "payment_intent.failed":
                handlePaymentIntentFailed(event);
                break;
            // Add other cases as needed
            default:
                logger.warn("Unhandled event type: {}", event.getType());
        }
    }
    private void handlePaymentIntentSucceeded(Event event) {
        // Process payment intent succeeded event
        event.getDataObjectDeserializer().getObject().ifPresent(paymentIntent -> {
            String paymentIntentId = ((PaymentIntent) paymentIntent).getId();
            // Update your order status in the database using paymentIntentId
            // Example: updateOrderStatus(paymentIntentId, "Paid");
            logger.info("Handled payment_intent.succeeded asynchronously.");
        });
    }

    private void handlePaymentIntentFailed(Event event) {
        // Process payment intent failed event
        logger.info("Handled payment_intent.failed asynchronously.");
    }
    public PaymentIntent createPaymentIntent(long amount, String currency) throws StripeException {
        return PaymentIntent.create(
                new PaymentIntentCreateParams.Builder()
                        .setAmount(amount)
                        .setCurrency(currency)
                        .build()
        );
    }

    public PaymentIntent confirmPaymentIntent(String paymentIntentId, String paymentMethodId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

        var params =
                PaymentIntentConfirmParams.builder()
                        .setPaymentMethod(paymentMethodId)
                        .build();

        return paymentIntent.confirm(params);
    }
}
