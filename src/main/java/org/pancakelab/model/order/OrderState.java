package org.pancakelab.model.order;

import org.pancakelab.model.order.exception.OrderStateException;
import org.pancakelab.model.pancake.Pancake;

public interface OrderState {

    /**
     * Adds a pancake to the order with the specified quantity.
     *
     * @param order    the order to which the pancake is added
     * @param pancake  the pancake to be added
     * @param quantity the quantity of the pancake to be added
     * @throws OrderStateException if the order is not in a state that allows adding pancakes
     */
    void addPancake(Order order, Pancake pancake, int quantity);

    /**
     * Removes a pancake from the order with the specified quantity.
     *
     * @param order    the order from which the pancake is removed
     * @param pancake  the pancake to be removed
     * @param quantity the quantity of the pancake to be removed
     * @throws OrderStateException if the order is not in a state that allows removing pancakes
     */
    void removePancake(Order order, Pancake pancake, int quantity);

    /**
     * Marks the order as cancelled.
     *
     * @param order the order to be cancelled
     * @throws OrderStateException if the order cannot be cancelled in its current state
     */
    void markCancelled(Order order);

    /**
     * Marks the order as completed.
     *
     * @param order the order to be completed
     * @throws OrderStateException if the order cannot be marked as completed in its current state
     */
    void markCompleted(Order order);

    /**
     * Marks the order as prepared and ready for delivery.
     *
     * @param order the order to be marked as prepared
     * @throws OrderStateException if the order cannot be marked as prepared in its current state
     */
    void markPrepared(Order order);

    /**
     * Marks the order as delivered to the customer.
     *
     * @param order the order to be marked as delivered
     * @throws OrderStateException if the order cannot be marked as delivered in its current state
     */
    void markDelivered(Order order);

    /**
     * Returns the current processing state of the order.
     *
     * @return the current order processing state
     */
    OrderProcessingState getState();
}
