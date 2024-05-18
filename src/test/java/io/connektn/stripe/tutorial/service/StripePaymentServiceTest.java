package io.connektn.stripe.tutorial.service;

import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.CardException;
import com.stripe.exception.StripeException;
import io.connektn.stripe.tutorial.PaymentService;
import io.connektn.stripe.tutorial.client.PaymentClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class StripePaymentServiceTest {

    private PaymentService service;

    private PaymentClient client;

    @BeforeEach
    public void setUp() {
        client = mock(PaymentClient.class);
        service = new PaymentService(client);
    }

    @Test
    public void shouldRetryPayment() throws StripeException {
        when(client.createPaymentIntent(anyLong(), anyString(), anyString())).thenThrow(apiConnectionException());
        var ex = assertThrows(RuntimeException.class, () -> service.charge(1000, "customer_id"));
        assertInstanceOf(ApiConnectionException.class, ex.getCause(), "Cause should be a CardException");
        verify(client, times(3)).createPaymentIntent(anyLong(), anyString(), anyString());
    }

    @Test
    public void shouldExitRetryLoopOnCardDecline() throws StripeException {
        when(client.createPaymentIntent(anyLong(), anyString(), anyString())).thenThrow(cardException());
        var ex = assertThrows(RuntimeException.class, () -> service.charge(1000, "customer_id"));
        assertInstanceOf(CardException.class, ex.getCause(), "Cause should be a CardException");
        verify(client, times(1)).createPaymentIntent(anyLong(), anyString(), anyString());
    }

    private ApiConnectionException apiConnectionException() {
        return new ApiConnectionException("Message describing the connection error");
    }

    private StripeException cardException() {
        return new CardException(
                "Message describing the card error", // message
                "req_id_from_stripe_dashboard",      // requestId
                "card_declined",                     // code
                "charge_id",                         // param (often used to indicate the field related to the error)
                "card_not_supported",                // decline code
                "30",                                // charge
                400,                                 // statusCode (use appropriate HTTP status code)
                null                                 // Throwable
        );
    }
}
