package org.pancakelab.service;

import org.junit.jupiter.api.*;
import org.pancakelab.model.order.Order;
import org.pancakelab.model.order.OrderProcessingState;
import org.pancakelab.model.pancake.PancakeMenu;
import org.pancakelab.repository.OrderRepository;
import org.pancakelab.repository.impl.InMemoryOrderRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test class covers the process of ordering pancakes, from order creation to order delivery.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class PancakeOrderingWorkflowTest {

    private static final String DARK_CHOCOLATE_PANCAKE_DESCRIPTION = "Delicious pancake with dark chocolate!";
    private static final String MILK_CHOCOLATE_PANCAKE_DESCRIPTION = "Delicious pancake with milk chocolate!";
    private static final String MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION = "Delicious pancake with milk chocolate, hazelnuts!";

    private final OrderRepository orderRepository = new InMemoryOrderRepository();
    private final PancakeMenu pancakeMenu = new PancakeMenu();
    private final PancakeService pancakeService = new PancakeService(orderRepository, pancakeMenu);

    private Order order = null;

    @Test
    @DisplayName("1. In the first step the Disciple creates an Order and specifies the building and the room number.")
    public void GivenOrderDoesNotExist_WhenCreatingOrder_ThenOrderCreatedWithCorrectData() {
        // given
        assertThat(order).isNull();

        // when
        order = pancakeService.createOrder(10, 20);

        // then
        assertThat(order.getDeliveryAddress()).satisfies(address -> {
            assertThat(address.building()).isEqualTo(10);
            assertThat(address.room()).isEqualTo(20);
        });
    }

    @Test
    @DisplayName("2. After that the Disciple can add pancakes from the menu.")
    public void GivenOrderExists_WhenAddingPancakes_ThenCorrectNumberOfPancakesAdded() {
        // when
        pancakeService.addPancakesToOrder(order, DARK_CHOCOLATE_PANCAKE_DESCRIPTION, 3);
        pancakeService.addPancakesToOrder(order, MILK_CHOCOLATE_PANCAKE_DESCRIPTION, 3);
        pancakeService.addPancakesToOrder(order, MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION, 3);

        // then
        Map<String, Integer> ordersPancakes = pancakeService.viewOrder(order);
        assertThat(ordersPancakes).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry(DARK_CHOCOLATE_PANCAKE_DESCRIPTION, 3),
                Map.entry(MILK_CHOCOLATE_PANCAKE_DESCRIPTION, 3),
                Map.entry(MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION, 3)
        ));
    }

    @Test
    @DisplayName("3. The Disciple can remove some pancakes from the order")
    public void GivenPancakesExists_WhenRemovingPancakes_ThenCorrectNumberOfPancakesRemoved() {
        // when
        pancakeService.removePancakesFromOrder(order, DARK_CHOCOLATE_PANCAKE_DESCRIPTION, 2);
        pancakeService.removePancakesFromOrder(order, MILK_CHOCOLATE_PANCAKE_DESCRIPTION, 3);
        pancakeService.removePancakesFromOrder(order, MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION, 1);

        // then
        Map<String, Integer> orderPancakes = pancakeService.viewOrder(order);
        assertThat(orderPancakes).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry(DARK_CHOCOLATE_PANCAKE_DESCRIPTION, 1),
                Map.entry(MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION, 2)
        ));
    }

    @Test
    @DisplayName("4. The Disciple can complete the Order")
    public void GivenOrderExists_WhenCompletingOrder_ThenOrderCompleted() {
        // when
        pancakeService.completeOrder(order);

        // then
        List<Order> completedOrdersOrders = pancakeService.getCompletedOrders();
        assertThat(completedOrdersOrders).contains(order);
    }

    @Test
    @DisplayName("5. After the Disciple completes the Order, the Chef can prepare the pancakes.")
    public void GivenOrderExists_WhenPreparingOrder_ThenOrderPrepared() {
        // when
        pancakeService.prepareOrder(order);

        // then
        List<Order> completedOrders = pancakeService.getCompletedOrders();
        assertThat(completedOrders).doesNotContain(order);

        List<Order> preparedOrders = pancakeService.getPreparedOrders();
        assertThat(preparedOrders).contains(order);
    }

    @Test
    @DisplayName("6. After the Chef prepares the Order it can be delivered.")
    public void GivenOrderExists_WhenDeliveringOrder_ThenCorrectOrderReturnedAndOrderRemovedFromTheDatabase() {
        // when
        pancakeService.deliverOrder(order);

        // then
        List<Order> preparedOrders = pancakeService.getPreparedOrders();
        assertThat(preparedOrders).doesNotContain(order);

        assertThat(order.getOrderProcessingState()).isEqualTo(OrderProcessingState.DELIVERED);
    }

    @Test
    @DisplayName("7. After the Order is sent for delivery it is removed from the database.")
    public void GivenOrderDelivered_WhenFindingOrder_ThenOrderNotFound() {
        // when
        Optional<Order> actualResult = orderRepository.findOrderById(order.getId());

        // then
        assertThat(actualResult).isEmpty();

        // tear down
        order = null;
    }

    // todo: refactor it
    @Test
    @DisplayName("8. Some test case with order cancellation")
    public void GivenOrderExists_WhenCancellingOrder_ThenOrderAndPancakesRemoved() {
        // given
        order = pancakeService.createOrder(10, 20);
        pancakeService.addPancakesToOrder(order, DARK_CHOCOLATE_PANCAKE_DESCRIPTION, 1);

        // when
        pancakeService.cancelOrder(order);

        // then
        List<Order> completedOrders = pancakeService.getCompletedOrders();
        assertThat(completedOrders).doesNotContain(order);

        List<Order> preparedOrders = pancakeService.getPreparedOrders();
        assertThat(preparedOrders).doesNotContain(order);

        assertThat(order.getOrderProcessingState()).isEqualTo(OrderProcessingState.CANCELLED);
    }
}
