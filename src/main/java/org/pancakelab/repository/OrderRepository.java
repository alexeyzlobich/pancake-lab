package org.pancakelab.repository;

import org.pancakelab.model.order.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    void saveOrder(Order order);

    Optional<Order> findOrderById(UUID orderId);

    void removeOrder(Order order);

    List<Order> findCompletedOrders();

    List<Order> findPreparedOrders();

}
