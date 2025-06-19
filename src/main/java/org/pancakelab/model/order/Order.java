package org.pancakelab.model.order;

import org.pancakelab.model.pancake.Pancake;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class Order {

    private static final Logger logger = Logger.getLogger(Order.class.getName());

    private final UUID id;
    private final Address deliveryAddress;
    private final List<OrderEntry> orderEntries;
    private OrderState state;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public Order(Address deliveryAddress) {
        if (deliveryAddress == null) {
            throw new IllegalArgumentException("Delivery address cannot be null");
        }
        this.id = UUID.randomUUID();
        this.deliveryAddress = deliveryAddress;
        this.orderEntries = new ArrayList<>();
        this.state = new NewOrderState();
    }

    public void addPancake(Pancake pancake, int quantity) {
        state.addPancake(this, pancake, quantity);
    }

    public void removePancake(Pancake pancake, int quantity) {
        state.removePancake(this, pancake, quantity);
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
        readLock.lock();
        try {
            return state.getState();
        } finally {
            readLock.unlock();
        }
    }

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
            throw new IllegalArgumentException("Quantity must be greater than zero");
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
                throw new IllegalStateException("Cannot complete an order with no pancakes.");
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
