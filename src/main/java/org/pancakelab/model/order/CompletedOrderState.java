package org.pancakelab.model.order;

import org.pancakelab.model.pancake.Pancake;

public class CompletedOrderState implements OrderState {

    @Override
    public void addPancake(Order order, Pancake pancake) {
        throw new IllegalStateException("Cannot add pancakes to a completed order.");
    }

    @Override
    public void removePancake(Order order, Pancake pancake) {
        throw new IllegalStateException("Cannot remove pancakes from a completed order.");
    }

    @Override
    public void markCancelled(Order order) {
        throw new IllegalStateException("Cannot cancel a completed order.");
    }

    @Override
    public void markCompleted(Order order) {
        // just do nothing
    }

    @Override
    public void markPrepared(Order order) {
        order.doMarkPrepared();
    }

    @Override
    public void markDelivered(Order order) {
        throw new IllegalStateException("Cannot deliver a completed order. Order must be prepared first.");
    }

    @Override
    public OrderProcessingState getState() {
        return OrderProcessingState.COMPLETED;
    }
}
