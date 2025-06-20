package org.pancakelab.model.order;

import org.pancakelab.model.order.exception.OrderCompletedException;
import org.pancakelab.model.pancake.Pancake;

public class CompletedOrderState implements OrderState {

    @Override
    public void addPancake(Order order, Pancake pancake, int quantity) {
        throw new OrderCompletedException("Cannot add pancakes to a completed order.");
    }

    @Override
    public void removePancake(Order order, Pancake pancake, int quantity) {
        throw new OrderCompletedException("Cannot remove pancakes from a completed order.");
    }

    @Override
    public void markCancelled(Order order) {
        throw new OrderCompletedException("Cannot cancel a completed order.");
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
        throw new OrderCompletedException("Cannot deliver a completed order. Order must be prepared first.");
    }

    @Override
    public OrderProcessingState getState() {
        return OrderProcessingState.COMPLETED;
    }
}
