package org.pancakelab.repository.impl;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.pancakelab.TestSamples;
import org.pancakelab.model.order.Order;
import org.pancakelab.repository.exception.DuplicatedIdException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class InMemoryOrderRepositoryTest {

    private final InMemoryOrderRepository testInstance = new InMemoryOrderRepository();

    @Nested
    class SaveOrder {

        @Test
        void ShouldSaveNewOrder_WhenOrderDoesNotExist() {
            // given
            Order order = TestSamples.newEmptyOrder();
            assertThat(testInstance.findOrderById(order.getId())).isEmpty();

            // when
            testInstance.saveOrder(order);

            // then
            assertThat(testInstance.findOrderById(order.getId())).hasValue(order);
        }

        @Test
        void ShouldThrowException_WhenOrderWithSameIdExists() {
            // given
            Order order = TestSamples.newEmptyOrder();
            testInstance.saveOrder(order);

            // when
            Exception exception = catchException(() -> testInstance.saveOrder(order));

            // then
            assertThat(exception)
                    .isInstanceOf(DuplicatedIdException.class)
                    .hasMessageContaining("Order with ID " + order.getId() + " already exists.");
        }
    }

    @Nested
    class FindOrderById {

        @Test
        void ShouldReturnEmpty_WhenOrderIsNotFound() {
            // given
            // add some order
            Order order = TestSamples.newEmptyOrder();
            testInstance.saveOrder(order);

            // when
            Optional<Order> actualResult = testInstance.findOrderById(UUID.randomUUID());

            // then
            assertThat(actualResult).isEmpty();
        }

        @Test
        void ShouldReturnOrder_WhenOrderExists() {
            // given
            Order order = TestSamples.newEmptyOrder();
            testInstance.saveOrder(order);

            // when
            var foundOrder = testInstance.findOrderById(order.getId());

            // then
            assertThat(foundOrder).hasValue(order);
        }

    }

    @Nested
    class RemoveOrder {

        @Test
        void ShouldRemoveOrder_WhenOrderExists() {
            // given
            Order order = TestSamples.newEmptyOrder();
            testInstance.saveOrder(order);

            // when
            testInstance.removeOrder(order);

            // then
            assertThat(testInstance.findOrderById(order.getId())).isEmpty();
        }

        @Test
        void ShouldDoNothing_WhenRemovingNonExistentOrder() {
            // given
            Order unsavedOrder = TestSamples.newEmptyOrder();

            // when
            testInstance.removeOrder(unsavedOrder);

            // then
            assertThat(testInstance.findOrderById(unsavedOrder.getId())).isEmpty();
        }

    }

    @Nested
    class FindCompletedOrders {

        @Test
        void ShouldReturnEmptyList_WhenNoCompletedOrders() {
            // given
            testInstance.saveOrder(TestSamples.newEmptyOrder());

            // when
            List<Order> completedOrders = testInstance.findCompletedOrders();

            // then
            assertThat(completedOrders).isEmpty();
        }

        @Test
        void ShouldReturnOnlyCompletedOrders_WhenCompletedOrdersExist() {
            // given
            Order order1 = TestSamples.completedOrder();
            testInstance.saveOrder(order1);

            Order order2 = TestSamples.preparedOrder();
            testInstance.saveOrder(order2);

            // when
            List<Order> completedOrders = testInstance.findCompletedOrders();

            // then
            assertThat(completedOrders).containsExactly(order1);
        }
    }

    @Nested
    class FindPreparedOrders {

        @Test
        void ShouldReturnEmptyList_WhenNoPreparedOrders() {
            // given
            testInstance.saveOrder(TestSamples.newEmptyOrder());

            // when
            List<Order> preparedOrders = testInstance.findPreparedOrders();

            // then
            assertThat(preparedOrders).isEmpty();
        }

        @Test
        void ShouldReturnOnlyPreparedOrders_WhenPreparedOrdersExist() {
            // given
            Order order1 = TestSamples.preparedOrder();
            testInstance.saveOrder(order1);

            Order order2 = TestSamples.completedOrder();
            testInstance.saveOrder(order2);

            // when
            List<Order> preparedOrders = testInstance.findPreparedOrders();

            // then
            assertThat(preparedOrders).containsExactly(order1);
        }
    }
}