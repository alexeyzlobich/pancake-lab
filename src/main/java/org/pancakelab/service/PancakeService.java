package org.pancakelab.service;

import org.pancakelab.model.order.Address;
import org.pancakelab.model.order.Order;
import org.pancakelab.model.order.exception.InvalidAddressException;
import org.pancakelab.model.pancake.exception.NoSuchPancakeException;
import org.pancakelab.model.order.exception.OrderStateException;
import org.pancakelab.model.pancake.Ingredient;
import org.pancakelab.model.pancake.Pancake;
import org.pancakelab.model.pancake.PancakeMenu;
import org.pancakelab.repository.OrderRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for managing pancake orders.
 */
public class PancakeService {

    private final OrderRepository orderRepository;
    private final PancakeMenu pancakeMenu;

    public PancakeService(OrderRepository orderRepository, PancakeMenu pancakeMenu) {
        this.orderRepository = orderRepository;
        this.pancakeMenu = pancakeMenu;
    }

    /**
     * Creates a new order for the specified location.
     *
     * @param building building number for delivery
     * @param room     room number for delivery
     * @return newly created order
     * @throws InvalidAddressException if the building or room number is invalid (e.g. less than or equal to 0)
     */
    public Order createOrder(int building, int room) {
        Order order = new Order(new Address(building, room));
        orderRepository.saveOrder(order);
        return order;
    }

    /**
     * Adds pancakes to an order by description from the menu.
     *
     * @param order              order to update
     * @param pancakeDescription description of the pancake to add
     * @param count              number of pancakes to add
     * @throws NoSuchPancakeException if the pancake description doesn't match any menu item
     * @throws OrderStateException    if the order is not in a state that allows adding pancakes
     */
    public void addPancakeToOrder(Order order, String pancakeDescription, int count) {
        Pancake pancakeFromMenu = pancakeMenu.findPancakeByDescription(pancakeDescription)
                .orElseThrow(() -> new NoSuchPancakeException("Sorry, there is no such pancake in menu"));

        order.addPancake(pancakeFromMenu, count);
    }

    /**
     * Adds custom pancakes to an order based on provided ingredients.
     *
     * @param order       order to update
     * @param ingredients list of ingredients for the custom pancake
     * @param count       number of pancakes to add
     * @throws OrderStateException if the order is not in a state that allows adding pancakes
     */
    public void addPancakeToOrder(Order order, List<Ingredient> ingredients, int count) {
        order.addPancake(new Pancake(ingredients), count);
    }

    /**
     * Removes pancakes from an order by description.
     *
     * @param order              order to update
     * @param pancakeDescription description of the pancake to remove
     * @param count              number of pancakes to remove
     */
    public void removePancakeFromOrder(Order order, String pancakeDescription, int count) {
        getPancakeFromOrderByDescription(order, pancakeDescription).ifPresent(pancake -> {
            order.removePancake(pancake, count);
        });
    }

    /**
     * Cancels an order.
     *
     * @param order order to cancel
     * @throws OrderStateException if the order cannot be cancelled in its current state
     */
    public void cancelOrder(Order order) {
        order.markCancelled();
        orderRepository.removeOrder(order);
    }

    /**
     * Completes an order.
     *
     * @param order order to be marked as completed
     * @throws OrderStateException if the order cannot be completed in its current state
     */
    public void completeOrder(Order order) {
        order.markCompleted();
    }

    public List<Order> getCompletedOrders() {
        return orderRepository.findCompletedOrders();
    }

    /**
     * Prepares an order.
     *
     * @param order order to be marked as prepared
     * @throws OrderStateException if the order cannot be prepared in its current state
     */
    public void prepareOrder(Order order) {
        order.markPrepared();
    }

    /**
     * Delivers an order.
     *
     * @param order order to be marked as delivered
     * @throws OrderStateException if the order cannot be delivered in its current state
     */
    public void deliverOrder(Order order) {
        order.markDelivered();
        orderRepository.removeOrder(order);
    }

    /**
     * Retrieves all orders that have been prepared and are ready for delivery.
     *
     * @return list of prepared orders
     */
    public List<Order> getPreparedOrders() {
        return orderRepository.findPreparedOrders();
    }

    /**
     * Creates a view of the order with pancake descriptions and counts.
     *
     * @param order order to view
     * @return map of pancake descriptions to quantities
     */
    public Map<String, Integer> viewOrder(Order order) {
        Map<Pancake, Integer> pancakesInOrder = order.getPancakes();
        Map<String, Integer> descriptionToReturn = new HashMap<>(pancakesInOrder.size());

        for (Map.Entry<Pancake, Integer> entry : pancakesInOrder.entrySet()) {
            descriptionToReturn.put(entry.getKey().getDescription(), entry.getValue());
        }
        return descriptionToReturn;
    }

    private Optional<Pancake> getPancakeFromOrderByDescription(Order order, String pancakeDescription) {
        return order.getPancakes()
                .keySet()
                .stream()
                .filter(pancake -> pancakeDescription.equals(pancake.getDescription()))
                .findFirst();
    }
}
