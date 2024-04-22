package io.connektn.stripe.tutorial.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Basket {
    private final List<BasketItem> items = new ArrayList<>();

    public void addItem(BasketItem item) {
        items.add(item);
    }

    public void removeItem(BasketItem item) {
        items.remove(item);
    }

    public long getTotalAmount() {
        return items.stream().mapToLong(item -> item.priceCents() * item.quantity()).sum();
    }

    public void clearBasket() {
        items.clear();
    }

    public List<BasketItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
