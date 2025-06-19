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
        void should_save_new_order() {
            // given
            Order order = TestSamples.newEmptyOrder();
            assertThat(testInstance.findOrderById(order.getId())).isEmpty();

            // when
            testInstance.saveOrder(order);

            // then
            assertThat(testInstance.findOrderById(order.getId())).hasValue(order);
        }

        @Test
        void should_throw_exception_when_order_with_same_id_already_exists() {
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
        void should_return_empty_when_order_is_not_found() {
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
        void should_return_order_when_it_exists() {
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
        void should_remove_order() {
            // given
            Order order = TestSamples.newEmptyOrder();
            testInstance.saveOrder(order);

            // when
            testInstance.removeOrder(order);

            // then
            assertThat(testInstance.findOrderById(order.getId())).isEmpty();
        }

        @Test
        void should_do_nothing_when_removing_non_existent_order() {
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
        void should_return_empty_list_when_no_completed_orders() {
            // given
            testInstance.saveOrder(TestSamples.newEmptyOrder());

            // when
            List<Order> completedOrders = testInstance.findCompletedOrders();

            // then
            assertThat(completedOrders).isEmpty();
        }

        @Test
        void should_return_only_completed_orders() {
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
        void should_return_empty_list_when_no_prepared_orders() {
            // given
            testInstance.saveOrder(TestSamples.newEmptyOrder());

            // when
            List<Order> preparedOrders = testInstance.findPreparedOrders();

            // then
            assertThat(preparedOrders).isEmpty();
        }

        @Test
        void should_return_only_prepared_orders() {
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