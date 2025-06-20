package org.pancakelab.model.order;

import org.pancakelab.model.order.exception.OrderCancelledException;
import org.pancakelab.model.pancake.Pancake;

public class CancelledOrderState implements OrderState {

    @Override
    public void addPancake(Order order, Pancake pancake, int quantity) {
        throw new OrderCancelledException("Cannot add pancakes to a cancelled order.");
    }

    @Override
    public void removePancake(Order order, Pancake pancake, int quantity) {
        throw new OrderCancelledException("Cannot remove pancakes from a cancelled order.");
    }

    @Override
    public void markCancelled(Order order) {
        // just do nothing
    }

    @Override
    public void markCompleted(Order order) {
        throw new OrderCancelledException("Cannot complete a cancelled order.");
    }

    @Override
    public void markPrepared(Order order) {
        throw new OrderCancelledException("Cannot prepare a cancelled order.");
    }

    @Override
    public void markDelivered(Order order) {
        throw new OrderCancelledException("Cannot deliver a cancelled order.");
    }

    @Override
    public OrderProcessingState getState() {
        return OrderProcessingState.CANCELLED;
    }
}
