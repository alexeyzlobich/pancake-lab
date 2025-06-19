package org.pancakelab.model.order;

import org.pancakelab.model.pancake.Pancake;

public class DeliveredOrderState implements OrderState {

    @Override
    public void addPancake(Order order, Pancake pancake, int quantity) {
        throw new IllegalStateException("Cannot add pancakes to a delivered order.");
    }

    @Override
    public void removePancake(Order order, Pancake pancake, int quantity) {
        throw new IllegalStateException("Cannot remove pancakes from a delivered order.");
    }

    @Override
    public void markCancelled(Order order) {
        throw new IllegalStateException("Cannot cancel a delivered order.");
    }

    @Override
    public void markCompleted(Order order) {
        throw new IllegalStateException("Cannot complete a delivered order.");
    }

    @Override
    public void markPrepared(Order order) {
        throw new IllegalStateException("Cannot prepare a delivered order.");
    }

    @Override
    public void markDelivered(Order order) {
        // just do nothing
    }

    @Override
    public OrderProcessingState getState() {
        return OrderProcessingState.DELIVERED;
    }
}
