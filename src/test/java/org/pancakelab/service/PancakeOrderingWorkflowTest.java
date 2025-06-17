package org.pancakelab.service;

import org.junit.jupiter.api.*;
import org.pancakelab.model.Order;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * This test class covers the process of ordering pancakes, from order creation to order delivery.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class PancakeOrderingWorkflowTest {

    private static final String DARK_CHOCOLATE_PANCAKE_DESCRIPTION = "Delicious pancake with dark chocolate!";
    private static final String MILK_CHOCOLATE_PANCAKE_DESCRIPTION = "Delicious pancake with milk chocolate!";
    private static final String MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION = "Delicious pancake with milk chocolate, hazelnuts!";

    private PancakeService pancakeService = new PancakeService();
    private Order order = null;

    @Test
    @DisplayName("1. In the first step the Disciple creates an Order and specifies the building and the room number.")
    public void GivenOrderDoesNotExist_WhenCreatingOrder_ThenOrderCreatedWithCorrectData() {
        // given
        assertThat(order).isNull();

        // when
        order = pancakeService.createOrder(10, 20);

        // then
        assertThat(order.getBuilding()).isEqualTo(10);
        assertThat(order.getRoom()).isEqualTo(20);
    }

    @Test
    @DisplayName("2. After that the Disciple can add pancakes from the menu.")
    public void GivenOrderExists_WhenAddingPancakes_ThenCorrectNumberOfPancakesAdded() {
        // given
        assertThat(order).isNotNull();

        // when
        addPancakes();

        // then
        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());
        assertThat(ordersPancakes).containsExactly(
                DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
                DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
                DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
                MILK_CHOCOLATE_PANCAKE_DESCRIPTION,
                MILK_CHOCOLATE_PANCAKE_DESCRIPTION,
                MILK_CHOCOLATE_PANCAKE_DESCRIPTION,
                MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION,
                MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION,
                MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION
        );
    }

    @Test
    @DisplayName("3. The Disciple can remove some pancakes from the order")
    public void GivenPancakesExists_WhenRemovingPancakes_ThenCorrectNumberOfPancakesRemoved() {
        // given
        List<String> orderPancakes = pancakeService.viewOrder(order.getId());
        assertThat(orderPancakes).contains(
                DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
                MILK_CHOCOLATE_PANCAKE_DESCRIPTION,
                MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION
        );

        // when
        pancakeService.removePancakes(DARK_CHOCOLATE_PANCAKE_DESCRIPTION, order.getId(), 2);
        pancakeService.removePancakes(MILK_CHOCOLATE_PANCAKE_DESCRIPTION, order.getId(), 3);
        pancakeService.removePancakes(MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION, order.getId(), 1);

        // then
        orderPancakes = pancakeService.viewOrder(order.getId());

        assertThat(orderPancakes).containsExactly(
                DARK_CHOCOLATE_PANCAKE_DESCRIPTION,
                MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION,
                MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION
        );
    }

    @Test
    @DisplayName("4. The Disciple can complete the Order")
    public void GivenOrderExists_WhenCompletingOrder_ThenOrderCompleted() {
        // when
        pancakeService.completeOrder(order.getId());

        // then
        Set<UUID> completedOrdersOrders = pancakeService.listCompletedOrders();
        assertThat(completedOrdersOrders).contains(order.getId());
    }

    @Test
    @DisplayName("5. When the Disciple completes the Order, the Chef can prepare the pancakes.")
    public void GivenOrderExists_WhenPreparingOrder_ThenOrderPrepared() {
        // when
        pancakeService.prepareOrder(order.getId());

        // then
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertThat(completedOrders).doesNotContain(order.getId());

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertThat(preparedOrders).contains(order.getId());
    }

    @Test
    @DisplayName("6. After the Chef prepares the Order it can be delivered.")
    public void GivenOrderExists_WhenDeliveringOrder_ThenCorrectOrderReturnedAndOrderRemovedFromTheDatabase() {
        // given
        List<String> pancakesToDeliver = pancakeService.viewOrder(order.getId());

        // when
        Object[] deliveredOrder = pancakeService.deliverOrder(order.getId());

        // then
        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());
        assertThat(ordersPancakes).isEmpty();
        // todo: refactor this
        assertEquals(order.getId(), ((Order) deliveredOrder[0]).getId());
        assertEquals(pancakesToDeliver, (List<String>) deliveredOrder[1]);
    }

    @Test
    @DisplayName("7. After the Order is sent for delivery it is removed from the database.")
    public void delivered_order_is_removed_from_database() {
        // when
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertThat(completedOrders).doesNotContain(order.getId());

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertThat(preparedOrders).doesNotContain(order.getId());

        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

        // tear down
        order = null;
    }

    // todo: refactor it
    @Test
    @DisplayName("8. Some test case with order cancellation")
    public void GivenOrderExists_WhenCancellingOrder_ThenOrderAndPancakesRemoved() {
        // given
        order = pancakeService.createOrder(10, 20);
        addPancakes();

        // when
        pancakeService.cancelOrder(order.getId());

        // then
        Set<UUID> completedOrders = pancakeService.listCompletedOrders();
        assertFalse(completedOrders.contains(order.getId()));

        Set<UUID> preparedOrders = pancakeService.listPreparedOrders();
        assertFalse(preparedOrders.contains(order.getId()));

        List<String> ordersPancakes = pancakeService.viewOrder(order.getId());

        assertEquals(List.of(), ordersPancakes);
    }

    private void addPancakes() {
        pancakeService.addDarkChocolatePancake(order.getId(), 3);
        pancakeService.addMilkChocolatePancake(order.getId(), 3);
        pancakeService.addMilkChocolateHazelnutsPancake(order.getId(), 3);
    }
}
