package io.connektn.stripe.tutorial.model;

public record Receipt(String id, long amount, String currency, String status) {
    public Receipt {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than 0");
        }
        if (currency == null) {
            throw new IllegalArgumentException("currency cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status cannot be null");
        }
    }
}
