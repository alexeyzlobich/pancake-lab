package org.pancakelab;

import org.pancakelab.model.order.Address;
import org.pancakelab.model.order.Order;
import org.pancakelab.model.pancake.Ingredient;
import org.pancakelab.model.pancake.Pancake;

import java.util.List;

public class TestSamples {

    public static Pancake pancake() {
        return new Pancake(List.of(Ingredient.DARK_CHOCOLATE));
    }

    public static Order newEmptyOrder() {
        return new Order(new Address(1, 1));
    }

    public static Order newOrderWithPancake() {
        Order order = new Order(new Address(1, 1));
        order.addPancake(pancake(), 1);
        return order;
    }

    public static Order cancelledOrder() {
        Order order = newOrderWithPancake();
        order.markCancelled();
        return order;
    }

    public static Order completedOrder() {
        Order order = newOrderWithPancake();
        order.markCompleted();
        return order;
    }

    public static Order preparedOrder() {
        Order order = completedOrder();
        order.markPrepared();
        return order;
    }

    public static Order deliveredOrder() {
        Order order = preparedOrder();
        order.markDelivered();
        return order;
    }

}
