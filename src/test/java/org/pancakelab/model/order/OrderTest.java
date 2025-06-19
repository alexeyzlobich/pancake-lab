package org.pancakelab.model.order;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.pancakelab.TestSamples;
import org.pancakelab.model.pancake.Pancake;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class OrderTest {

    @Nested
    class AddPancake {

        @Test
        void should_add_pancake() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.newEmptyOrder();

            // when
            order.addPancake(pancake);

            // then
            assertThat(order.getPancakes()).containsExactly(pancake);
        }

        @Test
        void should_be_able_to_add_the_same_pancake_multiple_times() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.newEmptyOrder();

            // when
            order.addPancake(pancake);
            order.addPancake(pancake);

            // then
            assertThat(order.getPancakes()).containsExactly(pancake, pancake);
        }

        @Test
        void should_throw_exception_when_order_is_completed() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.completedOrder();

            // when
            Exception exception = catchException(() -> order.addPancake(pancake));

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot add pancakes to a completed order");
        }

        @Test
        void should_throw_exception_when_order_is_cancelled() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.cancelledOrder();

            // when
            Exception exception = catchException(() -> order.addPancake(pancake));

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot add pancakes to a cancelled order");
        }

        @Test
        void should_throw_exception_when_order_is_prepared() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.preparedOrder();

            // when
            Exception exception = catchException(() -> order.addPancake(pancake));

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot add pancakes to a prepared order");
        }

        @Test
        void should_throw_exception_when_order_is_delivered() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.deliveredOrder();

            // when
            Exception exception = catchException(() -> order.addPancake(pancake));

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot add pancakes to a delivered order");
        }
    }

    @Nested
    class RemovePancake {

        @Test
        void should_remove_pancake() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.newEmptyOrder();
            order.addPancake(pancake);

            // when
            order.removePancake(pancake);

            // then
            assertThat(order.getPancakes()).isEmpty();
        }

        @Test
        void should_throw_exception_when_order_is_completed() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.completedOrder();

            // when
            Exception exception = catchException(() -> order.removePancake(pancake));

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot remove pancakes from a completed order");
        }

        @Test
        void should_throw_exception_when_order_is_cancelled() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.cancelledOrder();

            // when
            Exception exception = catchException(() -> order.removePancake(pancake));

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot remove pancakes from a cancelled order");
        }

        @Test
        void should_throw_exception_when_order_is_prepared() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.preparedOrder();

            // when
            Exception exception = catchException(() -> order.removePancake(pancake));

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot remove pancakes from a prepared order");
        }

        @Test
        void should_throw_exception_when_order_is_delivered() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.deliveredOrder();

            // when
            Exception exception = catchException(() -> order.removePancake(pancake));

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot remove pancakes from a delivered order");
        }

    }

    @Nested
    class MarkCancelled {

        @Test
        void should_mark_order_as_cancelled_when_order_is_empty() {
            // given
            Order order = TestSamples.newEmptyOrder();

            // when
            order.markCancelled();

            // then
            assertThat(order.getOrderProcessingState()).isEqualTo(OrderProcessingState.CANCELLED);
        }

        @Test
        void should_mark_order_as_cancelled_when_order_is_not_empty() {
            // given
            Order order = TestSamples.newOrderWithPancake();

            // when
            order.markCancelled();

            // then
            assertThat(order.getOrderProcessingState()).isEqualTo(OrderProcessingState.CANCELLED);
        }

        @Test
        void should_throw_exception_when_order_is_completed() {
            // given
            Order order = TestSamples.completedOrder();

            // when
            Exception exception = catchException(order::markCancelled);

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot cancel a completed order");
        }

        @Test
        void should_throw_exception_when_order_is_prepared() {
            // given
            Order order = TestSamples.preparedOrder();

            // when
            Exception exception = catchException(order::markCancelled);

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot cancel a prepared order");
        }

        @Test
        void should_throw_exception_when_order_is_delivered() {
            // given
            Order order = TestSamples.deliveredOrder();

            // when
            Exception exception = catchException(order::markCancelled);

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot cancel a delivered order");
        }
    }

    @Nested
    class MarkCompleted {

        @Test
        void should_mark_order_as_completed() {
            // given
            Order order = TestSamples.newOrderWithPancake();

            // when
            order.markCompleted();

            // then
            assertThat(order.getOrderProcessingState()).isEqualTo(OrderProcessingState.COMPLETED);
        }

        @Test
        void should_throw_exception_when_order_is_empty() {
            // given
            Order order = TestSamples.newEmptyOrder();

            // when
            Exception exception = catchException(order::markCompleted);

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot complete an order with no pancakes");
        }

        @Test
        void should_throw_exception_when_order_is_cancelled() {
            // given
            Order order = TestSamples.cancelledOrder();

            // when
            Exception exception = catchException(order::markCompleted);

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot complete a cancelled order");
        }

        @Test
        void should_throw_exception_when_order_is_prepared() {
            // given
            Order order = TestSamples.preparedOrder();

            // when
            Exception exception = catchException(order::markCompleted);

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot complete a prepared order");
        }

        @Test
        void should_throw_exception_when_order_is_delivered() {
            // given
            Order order = TestSamples.deliveredOrder();

            // when
            Exception exception = catchException(order::markCompleted);

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot complete a delivered order");
        }
    }

    @Nested
    class MarkPrepared {

        @Test
        void should_mark_order_as_prepared() {
            // given
            Order order = TestSamples.completedOrder();

            // when
            order.markPrepared();

            // then
            assertThat(order.getOrderProcessingState()).isEqualTo(OrderProcessingState.PREPARED);
        }

        @Test
        void should_throw_exception_when_order_is_not_completed() {
            // given
            Order order = TestSamples.newOrderWithPancake();

            // when
            Exception exception = catchException(order::markPrepared);

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot prepare a new order. Order must be completed first.");
        }

        @Test
        void should_throw_exception_when_order_is_cancelled() {
            // given
            Order order = TestSamples.cancelledOrder();

            // when
            Exception exception = catchException(order::markPrepared);

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot prepare a cancelled order");
        }

        @Test
        void should_throw_exception_when_order_is_delivered() {
            // given
            Order order = TestSamples.deliveredOrder();

            // when
            Exception exception = catchException(order::markPrepared);

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot prepare a delivered order");
        }
    }

    @Nested
    class MarkDelivered {

        @Test
        void should_mark_order_as_delivered() {
            // given
            Order order = TestSamples.preparedOrder();

            // when
            order.markDelivered();

            // then
            assertThat(order.getOrderProcessingState()).isEqualTo(OrderProcessingState.DELIVERED);
        }

        @Test
        void should_throw_exception_when_order_is_not_prepared() {
            // given
            Order order = TestSamples.completedOrder();

            // when
            Exception exception = catchException(order::markDelivered);

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot deliver a completed order. Order must be prepared first.");
        }

        @Test
        void should_throw_exception_when_order_is_cancelled() {
            // given
            Order order = TestSamples.cancelledOrder();

            // when
            Exception exception = catchException(order::markDelivered);

            // then
            assertThat(exception)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot deliver a cancelled order");
        }
    }
}