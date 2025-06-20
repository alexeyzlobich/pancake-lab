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
 * This test class covers an end-to-end process of cancelling orders.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class PancakeCancellationWorkflowTest {

    private static final String DARK_CHOCOLATE_PANCAKE_DESCRIPTION = "Delicious pancake with dark chocolate!";

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
        pancakeService.addPancakeToOrder(order, DARK_CHOCOLATE_PANCAKE_DESCRIPTION, 2);

        // then
        Map<String, Integer> ordersPancakes = pancakeService.viewOrder(order);
        assertThat(ordersPancakes).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry(DARK_CHOCOLATE_PANCAKE_DESCRIPTION, 2)
        ));
    }

    @Test
    @DisplayName("3. The Disciple can cancel the Order.")
    public void GivenOrderExists_WhenCancellingOrder_ThenOrderIsCancelled() {
        // when
        pancakeService.cancelOrder(order);

        // then
        assertThat(order.getOrderProcessingState()).isEqualTo(OrderProcessingState.CANCELLED);
    }

    @Test
    @DisplayName("4. After the Order is cancelled, it is removed from the database.")
    public void GivenOrderIsCancelled_WhenTryingToFindOrderInDatabase_ThenOrderIsNotFound() {
        // when/then
        Optional<Order> actualResult = orderRepository.findOrderById(order.getId());
        assertThat(actualResult).isEmpty();

        // when/then
        List<Order> completedOrders = pancakeService.getCompletedOrders();
        assertThat(completedOrders).doesNotContain(order);

        // when/then
        List<Order> preparedOrders = pancakeService.getPreparedOrders();
        assertThat(preparedOrders).doesNotContain(order);
    }

}
