package io.connektn.stripe.tutorial.client;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;

public class PaymentClient {
    PaymentClient(String secretKey) {
        Stripe.apiKey = secretKey;
    }

    public PaymentIntent createPaymentIntent(long amount, String customerId, String idempotencyKey) throws StripeException {
        var params = new PaymentIntentCreateParams.Builder()
                .setAmount(amount)
                .setCurrency("USD")
                .setCustomer(customerId)
                .build();

        var options = new RequestOptions.RequestOptionsBuilder()
                .setIdempotencyKey(idempotencyKey)
                .build();
        return PaymentIntent.create(params, options);
    }
}
