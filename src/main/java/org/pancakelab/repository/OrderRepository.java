package org.pancakelab.repository;

import org.pancakelab.model.order.Order;
import org.pancakelab.repository.exception.DuplicatedIdException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for performing CRUD operations on Order entities.
 */
public interface OrderRepository {

    /**
     * Saves an order to the repository.
     *
     * @param order the order to save, must not be null
     * @throws DuplicatedIdException if an order with the same ID already exists in the repository
     */
    void saveOrder(Order order);

    /**
     * Finds an order by its unique identifier.
     *
     * @param orderId the UUID of the order to find
     * @return an Optional containing the found order, or an empty Optional if no order exists with the given ID
     */
    Optional<Order> findOrderById(UUID orderId);

    /**
     * Removes an order from the repository.
     *
     * @param order the order to remove, must not be null
     */
    void removeOrder(Order order);

    /**
     * Retrieves all orders that have been completed.
     *
     * @return a list of all completed orders, or an empty list if no orders are completed
     */
    List<Order> findCompletedOrders();

    /**
     * Retrieves all orders that have been prepared but not yet completed.
     *
     * @return a list of all prepared orders, or an empty list if no orders are in the prepared state
     */
    List<Order> findPreparedOrders();

}
