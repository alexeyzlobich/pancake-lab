package org.pancakelab.model.order;

import org.pancakelab.model.pancake.Pancake;

import java.util.Objects;

public class OrderEntry {
    private final Pancake pancake;
    private int quantity;

    public OrderEntry(Pancake pancake, int quantity) {
        if (pancake == null) {
            throw new IllegalArgumentException("Pancake cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        this.pancake = pancake;
        this.quantity = quantity;
    }

    public Pancake getPancake() {
        return pancake;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntry that = (OrderEntry) o;
        return quantity == that.quantity && Objects.equals(pancake, that.pancake);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pancake, quantity);
    }
}
