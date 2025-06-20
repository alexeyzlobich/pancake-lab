package org.pancakelab.model.order;

import org.pancakelab.model.order.exception.InvalidAddressException;
import org.pancakelab.model.order.exception.InvalidQuantityException;
import org.pancakelab.model.order.exception.OrderStateException;
import org.pancakelab.model.pancake.Pancake;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

/**
 * Represents an order for pancakes.
 * <br/>
 * Thread-safe.
 */
public class Order {

    private static final Logger logger = Logger.getLogger(Order.class.getName());

    private final UUID id;
    private final Address deliveryAddress;
    private final List<OrderEntry> orderEntries;
    private OrderState state;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    /**
     * Creates a new order with the specified delivery address.
     *
     * @param deliveryAddress the address where the order should be delivered
     * @throws InvalidAddressException if the delivery address is not provided
     */
    public Order(Address deliveryAddress) {
        if (deliveryAddress == null) {
            throw new InvalidAddressException("Delivery address cannot be null");
        }
        this.id = UUID.randomUUID();
        this.deliveryAddress = deliveryAddress;
        this.orderEntries = new ArrayList<>();
        this.state = new NewOrderState();
    }

    /**
     * Adds a pancake to the order with the specified quantity.
     *
     * @param pancake  the pancake to be added
     * @param quantity the quantity of the pancake to be added
     * @throws OrderStateException      if the order is not in a state that allows adding pancakes
     * @throws InvalidQuantityException if the quantity is less than or equal to zero
     */
    public void addPancake(Pancake pancake, int quantity) {
        state.addPancake(this, pancake, quantity);
    }

    /**
     * Removes the specified quantity of a pancake from the order.
     *
     * @param pancake  the pancake to be removed
     * @param quantity the quantity to remove
     * @throws OrderStateException if the order is not in a state that allows removing pancakes
     */
    public void removePancake(Pancake pancake, int quantity) {
        state.removePancake(this, pancake, quantity);
    }

    /**
     * Marks the order as cancelled.
     *
     * @throws OrderStateException if the order cannot be cancelled in its current state
     */
    public void markCancelled() {
        state.markCancelled(this);
    }

    /**
     * Marks the order as completed.
     *
     * @throws OrderStateException if the order cannot be completed in its current state
     * @throws OrderStateException if the order has no pancakes
     */
    public void markCompleted() {
        state.markCompleted(this);
    }

    /**
     * Marks the order as prepared.
     *
     * @throws OrderStateException if the order cannot be marked as prepared in its current state
     */
    public void markPrepared() {
        state.markPrepared(this);
    }

    /**
     * Marks the order as delivered.
     *
     * @throws OrderStateException if the order cannot be marked as delivered in its current state
     */
    public void markDelivered() {
        state.markDelivered(this);
    }

    /**
     * Returns the unique identifier of this order.
     *
     * @return the order's unique identifier
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the delivery address for this order.
     *
     * @return the delivery address
     */
    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    /**
     * Returns the current processing state of the order.
     *
     * @return the order's current processing state
     */
    public OrderProcessingState getOrderProcessingState() {
        readLock.lock();
        try {
            return state.getState();
        } finally {
            readLock.unlock();
        }
    }

    /**
     * @return an unmodifiable map where keys are pancakes and values are their quantities
     */
    public Map<Pancake, Integer> getPancakes() {
        readLock.lock();
        try {
            Map<Pancake, Integer> pancakes = new HashMap<>(orderEntries.size());
            for (OrderEntry orderEntry : orderEntries) {
                pancakes.put(orderEntry.getPancake(), orderEntry.getQuantity());
            }
            return Collections.unmodifiableMap(pancakes);
        } finally {
            readLock.unlock();
        }
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

    void doAddPancake(Pancake pancake, int quantity) {
        if (pancake == null) {
            throw new IllegalArgumentException("Pancake cannot be null");
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be greater than zero");
        }

        writeLock.lock();
        try {
            OrderEntry existingOrderEntry = findExistingOrderEntryForPancake(pancake);
            if (existingOrderEntry != null) {
                existingOrderEntry.setQuantity(existingOrderEntry.getQuantity() + quantity);
            } else {
                orderEntries.add(new OrderEntry(pancake, quantity));
            }
        } finally {
            writeLock.unlock();
        }
        logger.info(() -> "Added " + quantity + " pancake(s) with description '" + pancake.getDescription() + "' " +
                "to order " + id + ".");
    }

    void doRemovePancake(Pancake pancake, int quantity) {
        if (pancake == null) {
            throw new IllegalArgumentException("Pancake cannot be null");
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be greater than zero");
        }

        writeLock.lock();
        try {
            OrderEntry existingOrderEntry = findExistingOrderEntryForPancake(pancake);
            if (existingOrderEntry != null) {
                int newQuantity = existingOrderEntry.getQuantity() - quantity;
                if (newQuantity <= 0) {
                    orderEntries.remove(existingOrderEntry);
                } else {
                    existingOrderEntry.setQuantity(newQuantity);
                }
            } else {
                logger.warning(() -> "Attempted to remove pancake with description '" + pancake.getDescription() +
                        "' from order " + id + ", but it was not found in the order.");
            }
        } finally {
            writeLock.unlock();
        }
        logger.info(() -> "Removed " + quantity + " pancake(s) with description '" + pancake.getDescription() + "' " +
                "from order " + id + ".");
    }

    void doMarkCancelled() {
        writeLock.lock();
        try {
            state = new CancelledOrderState();
        } finally {
            writeLock.unlock();
        }
        logger.info(() -> "Order " + id + " cancelled.");
    }

    void doMarkCompleted() {
        writeLock.lock();
        try {
            if (orderEntries.isEmpty()) {
                throw new OrderStateException("Cannot complete an order with no pancakes.");
            }
            state = new CompletedOrderState();
        } finally {
            writeLock.unlock();
        }
        logger.info(() -> "Order " + id + " completed.");
    }

    void doMarkPrepared() {
        writeLock.lock();
        try {
            state = new PreparedOrderState();
        } finally {
            writeLock.unlock();
        }
        logger.info(() -> "Order " + id + " prepared.");
    }

    void doMarkDelivered() {
        writeLock.lock();
        try {
            state = new DeliveredOrderState();
        } finally {
            writeLock.unlock();
        }
        logger.info(() -> "Order " + id + " delivered.");
    }

    private OrderEntry findExistingOrderEntryForPancake(Pancake pancake) {
        for (OrderEntry orderEntry : orderEntries) {
            if (orderEntry.getPancake().equals(pancake)) {
                return orderEntry;
            }
        }
        return null;
    }
}
