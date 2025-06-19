package org.pancakelab.service;

import org.pancakelab.model.order.Address;
import org.pancakelab.model.order.Order;
import org.pancakelab.model.pancake.Ingredient;
import org.pancakelab.model.pancake.Pancake;
import org.pancakelab.model.pancake.PancakeMenu;
import org.pancakelab.repository.OrderRepository;

import java.util.List;

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
        for (int i = 0; i < count; i++) {
            Pancake pancakeFromMenu = pancakeMenu.findPancakeByDescription(pancakeDescription)
                    .orElseThrow(() -> new RuntimeException("Sorry, there is no such pancake in menu"));
            order.addPancake(pancakeFromMenu);
        }
    }

    public void addPancakesToOrder(Order order, List<Ingredient> ingredients, int count) {
        for (int i = 0; i < count; i++) {
            order.addPancake(new Pancake(ingredients));
        }
    }

    public void removePancakesFromOrder(Order order, String pancakeDescription, int count) {
        order.getPancakes()
                .stream()
                .filter(pancake -> pancakeDescription.equals(pancake.getDescription()))
                .limit(count)
                .forEach(order::removePancake);
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

    public List<String> viewOrder(Order order) {
        return order.getPancakes()
                .stream()
                .map(Pancake::getDescription)
                .toList();
    }
}
