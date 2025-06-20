package org.pancakelab.model.order;

import org.pancakelab.model.order.exception.NewOrderException;
import org.pancakelab.model.pancake.Pancake;

public class NewOrderState implements OrderState {

    @Override
    public void addPancake(Order order, Pancake pancake, int quantity) {
        order.doAddPancake(pancake, quantity);
    }

    @Override
    public void removePancake(Order order, Pancake pancake, int quantity) {
        order.doRemovePancake(pancake, quantity);
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
        throw new NewOrderException("Cannot prepare a new order. Order must be completed first.");
    }

    @Override
    public void markDelivered(Order order) {
        throw new NewOrderException("Cannot deliver a new order. Order must be prepared first.");
    }

    @Override
    public OrderProcessingState getState() {
        return OrderProcessingState.NEW;
    }
}
