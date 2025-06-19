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
    private OrderState state;

    public Order(Address deliveryAddress) {
        if (deliveryAddress == null) {
            throw new IllegalArgumentException("Delivery address cannot be null");
        }
        this.id = UUID.randomUUID();
        this.deliveryAddress = deliveryAddress;
        this.pancakes = new ArrayList<>();
        this.state = new NewOrderState();
    }

    public void addPancake(Pancake pancake) {
        state.addPancake(this, pancake);
    }

    public void removePancake(Pancake pancake) {
        state.removePancake(this, pancake);
    }

    public void markCancelled() {
        state.markCancelled(this);
    }

    public void markCompleted() {
        state.markCompleted(this);
    }

    public void markPrepared() {
        state.markPrepared(this);
    }

    public void markDelivered() {
        state.markDelivered(this);
    }

    public UUID getId() {
        return id;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public OrderProcessingState getOrderProcessingState() {
        return state.getState();
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

    // --- State dependent methods/actions

    void doAddPancake(Pancake pancake) {
        if (pancake == null) {
            throw new IllegalArgumentException("Pancake cannot be null");
        }
        this.pancakes.add(pancake);
        logger.info(() -> "Added pancake with description '" + pancake.getDescription() + "' " +
                "to order " + id + " containing " + pancakes.size() + " pancakes.");
    }

    void doRemovePancake(Pancake pancake) {
        if (pancake == null) {
            throw new IllegalArgumentException("Pancake cannot be null");
        }
        this.pancakes.remove(pancake);
        logger.info(() -> "Removed pancake with description '" + pancake.getDescription() + "' " +
                "from order " + id + " now containing " + pancakes.size() + " pancakes.");
    }

    void doMarkCancelled() {
        state = new CancelledOrderState();
        logger.info(() -> "Order " + id + " cancelled.");
    }

    void doMarkCompleted() {
        if (pancakes.isEmpty()) {
            throw new IllegalStateException("Cannot complete an order with no pancakes.");
        }
        state = new CompletedOrderState();
        logger.info(() -> "Order " + id + " completed.");
    }

    void doMarkPrepared() {
        state = new PreparedOrderState();
        logger.info(() -> "Order " + id + " prepared.");
    }

    void doMarkDelivered() {
        state = new DeliveredOrderState();
        logger.info(() -> "Order " + id + " delivered.");
    }
}
