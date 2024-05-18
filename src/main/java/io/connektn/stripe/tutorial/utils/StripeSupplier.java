package io.connektn.stripe.tutorial.utils;

import com.stripe.exception.StripeException;

@FunctionalInterface
public interface StripeSupplier<T> {
    T get() throws StripeException;
}
