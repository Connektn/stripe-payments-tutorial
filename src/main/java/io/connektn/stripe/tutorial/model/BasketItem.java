package io.connektn.stripe.tutorial.model;

public record BasketItem(
        String id,
        String name,
        int quantity,
        long priceCents    // Pricing in cents to avoid floating point arithmetic issues
) {}
