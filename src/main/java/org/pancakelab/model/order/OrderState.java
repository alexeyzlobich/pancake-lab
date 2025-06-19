package org.pancakelab.model.order;

import org.pancakelab.model.pancake.Pancake;

public interface OrderState {

    void addPancake(Order order, Pancake pancake, int quantity);

    void removePancake(Order order, Pancake pancake, int quantity);

    void markCancelled(Order order);

    void markCompleted(Order order);

    void markPrepared(Order order);

    void markDelivered(Order order);

    OrderProcessingState getState();
}
