package io.connektn.stripe.tutorial;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    public StripeService(
            @Value("stripe.secret-key")
            String secretKey
    ) {
        Stripe.apiKey = secretKey;
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
