package org.pancakelab.model.order;

import org.pancakelab.model.order.exception.OrderPreparedException;
import org.pancakelab.model.pancake.Pancake;

public class PreparedOrderState implements OrderState {

    @Override
    public void addPancake(Order order, Pancake pancake, int quantity) {
        throw new OrderPreparedException("Cannot add pancakes to a prepared order.");
    }

    @Override
    public void removePancake(Order order, Pancake pancake, int quantity) {
        throw new OrderPreparedException("Cannot remove pancakes from a prepared order.");
    }

    @Override
    public void markCancelled(Order order) {
        throw new OrderPreparedException("Cannot cancel a prepared order.");
    }

    @Override
    public void markCompleted(Order order) {
        throw new OrderPreparedException("Cannot complete a prepared order.");
    }

    @Override
    public void markPrepared(Order order) {
        // just do nothing
    }

    @Override
    public void markDelivered(Order order) {
        order.doMarkDelivered();
    }

    @Override
    public OrderProcessingState getState() {
        return OrderProcessingState.PREPARED;
    }
}
