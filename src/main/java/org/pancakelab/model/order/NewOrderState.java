package org.pancakelab.model.order;

import org.pancakelab.model.pancake.Pancake;

public class NewOrderState implements OrderState {

    @Override
    public void addPancake(Order order, Pancake pancake) {
        order.doAddPancake(pancake);
    }

    @Override
    public void removePancake(Order order, Pancake pancake) {
        order.doRemovePancake(pancake);
    }

    @Override
    public void markCancelled(Order order) {
        order.doMarkCancelled();
    }

    @Override
    public void markCompleted(Order order) {
        order.doMarkCompleted();
    }

    @Override
    public void markPrepared(Order order) {
        throw new IllegalStateException("Cannot prepare a new order. Order must be completed first.");
    }

    @Override
    public void markDelivered(Order order) {
        throw new IllegalStateException("Cannot deliver a new order. Order must be prepared first.");
    }

    @Override
    public OrderProcessingState getState() {
        return OrderProcessingState.NEW;
    }
}
