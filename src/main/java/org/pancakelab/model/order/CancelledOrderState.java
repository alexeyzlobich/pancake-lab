package org.pancakelab.model.order;

import org.pancakelab.model.pancake.Pancake;

public class CancelledOrderState implements OrderState {

    @Override
    public void addPancake(Order order, Pancake pancake) {
        throw new IllegalStateException("Cannot add pancakes to a cancelled order.");
    }

    @Override
    public void removePancake(Order order, Pancake pancake) {
        throw new IllegalStateException("Cannot remove pancakes from a cancelled order.");
    }

    @Override
    public void markCancelled(Order order) {
        // just do nothing
    }

    @Override
    public void markCompleted(Order order) {
        throw new IllegalStateException("Cannot complete a cancelled order.");
    }

    @Override
    public void markPrepared(Order order) {
        throw new IllegalStateException("Cannot prepare a cancelled order.");
    }

    @Override
    public void markDelivered(Order order) {
        throw new IllegalStateException("Cannot deliver a cancelled order.");
    }

    @Override
    public OrderProcessingState getState() {
        return OrderProcessingState.CANCELLED;
    }
}
