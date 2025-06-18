package org.pancakelab.model.order;

import org.pancakelab.model.pancake.Pancake;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

public class Order {

    private static final Logger logger = Logger.getLogger(Order.class.getName());

    private final UUID id;
    private final Address deliveryAddress;
    private final List<Pancake> pancakes;
    private OrderProcessingState orderProcessingState;

    public Order(Address deliveryAddress) {
        if (deliveryAddress == null) {
            throw new IllegalArgumentException("Delivery address cannot be null");
        }
        this.id = UUID.randomUUID();
        this.deliveryAddress = deliveryAddress;
        this.pancakes = new ArrayList<>();
        this.orderProcessingState = OrderProcessingState.NEW;
    }

    public void addPancake(Pancake pancake) {
        if (pancake == null) {
            throw new IllegalArgumentException("Pancake cannot be null");
        }
        this.pancakes.add(pancake);
        logger.info(() -> "Added pancake with description '" + pancake.getDescription() + "' " +
                "to order " + id + " containing " + pancakes.size() + " pancakes.");
    }

    public void removePancake(Pancake pancake) {
        if (pancake == null) {
            throw new IllegalArgumentException("Pancake cannot be null");
        }
        this.pancakes.remove(pancake);
        logger.info(() -> "Removed pancake with description '" + pancake.getDescription() + "' " +
                "from order " + id + " now containing " + pancakes.size() + " pancakes.");
    }

    public void markCancelled() {
        this.orderProcessingState = OrderProcessingState.CANCELLED;
        logger.info(() -> "Order " + id + " cancelled.");
    }

    public void markDelivered() {
        this.orderProcessingState = OrderProcessingState.DELIVERED;
        logger.info(() -> "Order " + id + " delivered.");
    }

    public void markCompleted() {
        this.orderProcessingState = OrderProcessingState.COMPLETED;
        logger.info(() -> "Order " + id + " completed.");
    }

    public void markPrepared() {
        this.orderProcessingState = OrderProcessingState.PREPARED;
        logger.info(() -> "Order " + id + " prepared.");
    }

    public UUID getId() {
        return id;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public OrderProcessingState getOrderProcessingState() {
        return orderProcessingState;
    }

    public List<Pancake> getPancakes() {
        return List.copyOf(pancakes);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
