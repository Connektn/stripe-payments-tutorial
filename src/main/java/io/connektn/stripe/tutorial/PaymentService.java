package io.connektn.stripe.tutorial;

import io.connektn.stripe.tutorial.client.PaymentClient;
import io.connektn.stripe.tutorial.model.Receipt;

import java.util.UUID;

import static io.connektn.stripe.tutorial.utils.SafeRetryManager.withRetry;

public class PaymentService {

    private final PaymentClient client;

    public PaymentService(PaymentClient client) {
        this.client = client;
    }

    public Receipt charge(long amount, String customerId) {
        var idempotencyKey = UUID.randomUUID().toString();
        return withRetry(() -> {
            var paymentIntent = client.createPaymentIntent(amount, customerId, idempotencyKey);
            return new Receipt(
                    paymentIntent.getId(),
                    paymentIntent.getAmount(),
                    paymentIntent.getCurrency(),
                    paymentIntent.getStatus()
            );
        });
    }
}
