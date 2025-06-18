package org.pancakelab.repository.impl;

import org.pancakelab.model.order.Order;
import org.pancakelab.model.order.OrderProcessingState;
import org.pancakelab.repository.OrderRepository;
import org.pancakelab.repository.exception.DuplicatedIdException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryOrderRepository implements OrderRepository {

    private final ConcurrentMap<UUID, Order> ordersStorage = new ConcurrentHashMap<>();

    @Override
    public void saveOrder(Order order) {
        boolean isAdded = ordersStorage.putIfAbsent(order.getId(), order) == null;
        if (!isAdded) {
            throw new DuplicatedIdException("Order with ID " + order.getId() + " already exists.");
        }
    }

    @Override
    public Optional<Order> findOrderById(UUID orderId) {
        return Optional.ofNullable(ordersStorage.get(orderId));
    }

    @Override
    public void removeOrder(Order order) {
        ordersStorage.remove(order.getId());
    }

    @Override
    public List<Order> findCompletedOrders() {
        return ordersStorage.values().stream()
                .filter(order -> order.getOrderProcessingState() == OrderProcessingState.COMPLETED)
                .toList();
    }

    @Override
    public List<Order> findPreparedOrders() {
        return ordersStorage.values().stream()
                .filter(order -> order.getOrderProcessingState() == OrderProcessingState.PREPARED)
                .toList();
    }
}
