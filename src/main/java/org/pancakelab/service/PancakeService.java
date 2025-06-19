package org.pancakelab.service;

import org.pancakelab.model.order.Address;
import org.pancakelab.model.order.Order;
import org.pancakelab.model.pancake.Ingredient;
import org.pancakelab.model.pancake.Pancake;
import org.pancakelab.model.pancake.PancakeMenu;
import org.pancakelab.repository.OrderRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PancakeService {

    private final OrderRepository orderRepository;
    private final PancakeMenu pancakeMenu;

    public PancakeService(OrderRepository orderRepository, PancakeMenu pancakeMenu) {
        this.orderRepository = orderRepository;
        this.pancakeMenu = pancakeMenu;
    }

    public Order createOrder(int building, int room) {
        Order order = new Order(new Address(building, room));
        orderRepository.saveOrder(order);
        return order;
    }

    public void addPancakesToOrder(Order order, String pancakeDescription, int count) {
        Pancake pancakeFromMenu = pancakeMenu.findPancakeByDescription(pancakeDescription)
                .orElseThrow(() -> new RuntimeException("Sorry, there is no such pancake in menu"));

        order.addPancake(pancakeFromMenu, count);
    }

    public void addPancakesToOrder(Order order, List<Ingredient> ingredients, int count) {
        order.addPancake(new Pancake(ingredients), count);
    }

    public void removePancakesFromOrder(Order order, String pancakeDescription, int count) {
        Pancake pancakeFromMenu = pancakeMenu.findPancakeByDescription(pancakeDescription)
                .orElseThrow(() -> new RuntimeException("Sorry, there is no such pancake in menu"));

        order.removePancake(pancakeFromMenu, count);
    }

    public void cancelOrder(Order order) {
        order.markCancelled();
        orderRepository.removeOrder(order);
    }

    public void completeOrder(Order order) {
        order.markCompleted();
    }

    public List<Order> getCompletedOrders() {
        return orderRepository.findCompletedOrders();
    }

    public void prepareOrder(Order order) {
        order.markPrepared();
    }

    public List<Order> getPreparedOrders() {
        return orderRepository.findPreparedOrders();
    }

    public void deliverOrder(Order order) {
        order.markDelivered();
        orderRepository.removeOrder(order);
    }

    public Map<String, Integer> viewOrder(Order order) {
        Map<Pancake, Integer> pancakesInOrder = order.getPancakes();
        Map<String, Integer> descriptionToReturn = new HashMap<>(pancakesInOrder.size());

        for (Map.Entry<Pancake, Integer> entry : pancakesInOrder.entrySet()) {
            descriptionToReturn.put(entry.getKey().getDescription(), entry.getValue());
        }
        return descriptionToReturn;
    }
}
