package io.connektn.stripe.tutorial.utils;

import com.stripe.exception.CardException;
import com.stripe.exception.StripeException;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class SafeRetryManager {

    private final Random random = new Random();

    public static <T> T withRetry(StripeSupplier<T> supplier) {
        return new SafeRetryManager().withRetry(3, 100, supplier);
    }

    public <T> T withRetry(int maxRetries, int initialDelay, StripeSupplier<T> supplier) {
        int attempt = 0;
        try (var scheduler = Executors.newScheduledThreadPool(1)) {
            while (true) {
                try {
                    return supplier.get();
                } catch (CardException e) {
                    // No point in retrying if the error is due to the card
                    // Capture as many or as few card errors as needed
                    switch (e.getCode()) {
                        case "approve_with_id":
                            throw new RuntimeException("The payment can’t be authorised.", e);
                        case "expired_card":
                            throw new RuntimeException("The card has expired.", e);
                        case "card_not_supported":
                            throw new RuntimeException("The card does not support this type of purchase.", e);
                        default:
                            throw new RuntimeException("Unexpected card error", e);
                    }
                } catch (StripeException e) {
                    if (++attempt >= maxRetries) {
                        throw new RuntimeException("Operation failed after " + attempt + " attempts", e);
                    }
                    long waitTime = calculateDelay(initialDelay, 2.0, attempt, 0.1);
                    try {
                        scheduler.schedule(() -> System.out.println("Waiting for " + waitTime + " ms"), waitTime, TimeUnit.MILLISECONDS);
                    } catch (RejectedExecutionException ex) {
                        throw new RuntimeException("Retry attempt failed", ex);
                    }
                }
            }
        }
    }

    private long calculateDelay(long initialDelay, double factor, int attempt, double jitter) {
        // Calculate exponential delay
        long delay = (long) (initialDelay * Math.pow(factor, attempt - 1));

        // Apply jitter
        long jitterValue = (long) (delay * jitter * (random.nextDouble() - 0.5) * 2);
        return Math.max(0, delay + jitterValue);
    }
}
