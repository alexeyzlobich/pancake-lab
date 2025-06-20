package org.pancakelab.model.order;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.pancakelab.TestSamples;
import org.pancakelab.model.order.exception.*;
import org.pancakelab.model.pancake.Ingredient;
import org.pancakelab.model.pancake.Pancake;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    @Nested
    class AddPancake {

        @Test
        void ShouldAddPancake_WhenOrderIsEmpty() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.newEmptyOrder();

            // when
            order.addPancake(pancake, 2);

            // then
            assertThat(order.getPancakes()).containsExactly(Map.entry(pancake, 2));
        }

        @Test
        void ShouldAddPancake_WhenOrderIsNotEmpty() {
            // given
            Pancake anotherPancake = new Pancake(List.of(Ingredient.HAZELNUTS));
            Order order = TestSamples.newOrderWithPancake();

            // when
            order.addPancake(anotherPancake, 2);

            // then
            assertThat(order.getPancakes()).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                    Map.entry(TestSamples.pancake(), 1),
                    Map.entry(anotherPancake, 2)
            ));
        }

        @Test
        void ShouldAddTheSamePancake_WhenAddedMultipleTimes() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.newEmptyOrder();

            // when
            order.addPancake(pancake, 1);
            order.addPancake(pancake, 1);

            // then
            assertThat(order.getPancakes()).containsExactly(Map.entry(pancake, 2));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void ShouldThrowException_WhenQuantityIsInvalid(int invalidQuantityValue) {
            // given
            Order order = TestSamples.newEmptyOrder();
            Pancake pancake = TestSamples.pancake();

            // when
            Exception exception = catchException(() -> order.addPancake(pancake, invalidQuantityValue));

            // then
            assertThat(exception)
                    .isInstanceOf(InvalidQuantityException.class)
                    .hasMessageContaining("Quantity must be greater than zero");
        }

        @Test
        void ShouldThrowException_WhenOrderIsCompleted() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.completedOrder();

            // when
            Exception exception = catchException(() -> order.addPancake(pancake, 1));

            // then
            assertThat(exception)
                    .isInstanceOf(OrderCompletedException.class)
                    .hasMessageContaining("Cannot add pancakes to a completed order");
        }

        @Test
        void ShouldThrowException_WhenOrderIsCancelled() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.cancelledOrder();

            // when
            Exception exception = catchException(() -> order.addPancake(pancake, 1));

            // then
            assertThat(exception)
                    .isInstanceOf(OrderCancelledException.class)
                    .hasMessageContaining("Cannot add pancakes to a cancelled order");
        }

        @Test
        void ShouldThrowException_WhenOrderIsPrepared() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.preparedOrder();

            // when
            Exception exception = catchException(() -> order.addPancake(pancake, 1));

            // then
            assertThat(exception)
                    .isInstanceOf(OrderPreparedException.class)
                    .hasMessageContaining("Cannot add pancakes to a prepared order");
        }

        @Test
        void ShouldThrowException_WhenOrderIsDelivered() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.deliveredOrder();

            // when
            Exception exception = catchException(() -> order.addPancake(pancake, 1));

            // then
            assertThat(exception)
                    .isInstanceOf(OrderDeliveredException.class)
                    .hasMessageContaining("Cannot add pancakes to a delivered order");
        }

        @RepeatedTest(10)
        void ShouldBeThreadSafe() throws InterruptedException {
            // given
            Order order = TestSamples.newEmptyOrder();
            Pancake pancake = TestSamples.pancake();

            int nThreads = 100;
            ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
            CyclicBarrier startingPoint = new CyclicBarrier(nThreads);

            // when
            IntStream.range(0, nThreads).forEach((i) -> executorService.execute(() -> {
                await(startingPoint);
                order.addPancake(pancake, 1);
            }));

            // then
            awaitTermination(executorService);

            assertThat(order.getPancakes()).containsExactly(Map.entry(pancake, 100));
        }
    }

    @Nested
    class RemovePancake {

        @Test
        void ShouldRemovePancakeCompletely_WhenQuantityMatches() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.newEmptyOrder();
            order.addPancake(pancake, 2);

            // when
            order.removePancake(pancake, 2);

            // then
            assertThat(order.getPancakes()).isEmpty();
        }

        @Test
        void ShouldDecreasePancakeQuantity_WhenRemovingPartially() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.newEmptyOrder();
            order.addPancake(pancake, 2);

            // when
            order.removePancake(pancake, 1);

            // then
            assertThat(order.getPancakes()).containsExactly(Map.entry(pancake, 1));
        }

        @Test
        void ShouldRemovePancakeCompletely_WhenRemovingMoreThanExist() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.newEmptyOrder();
            order.addPancake(pancake, 2);

            // when
            order.removePancake(pancake, 3);

            // then
            assertThat(order.getPancakes()).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        void ShouldThrowException_WhenQuantityIsInvalid(int invalidQuantityValue) {
            // given
            Order order = TestSamples.newOrderWithPancake();
            Pancake pancake = TestSamples.pancake();

            // when
            Exception exception = catchException(() -> order.removePancake(pancake, invalidQuantityValue));

            // then
            assertThat(exception)
                    .isInstanceOf(InvalidQuantityException.class)
                    .hasMessageContaining("Quantity must be greater than zero");
        }

        @Test
        void ShouldThrowException_WhenOrderIsCompleted() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.completedOrder();

            // when
            Exception exception = catchException(() -> order.removePancake(pancake, 1));

            // then
            assertThat(exception)
                    .isInstanceOf(OrderCompletedException.class)
                    .hasMessageContaining("Cannot remove pancakes from a completed order");
        }

        @Test
        void ShouldThrowException_WhenOrderIsCancelled() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.cancelledOrder();

            // when
            Exception exception = catchException(() -> order.removePancake(pancake, 1));

            // then
            assertThat(exception)
                    .isInstanceOf(OrderCancelledException.class)
                    .hasMessageContaining("Cannot remove pancakes from a cancelled order");
        }

        @Test
        void ShouldThrowException_WhenOrderIsPrepared() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.preparedOrder();

            // when
            Exception exception = catchException(() -> order.removePancake(pancake, 1));

            // then
            assertThat(exception)
                    .isInstanceOf(OrderPreparedException.class)
                    .hasMessageContaining("Cannot remove pancakes from a prepared order");
        }

        @Test
        void ShouldThrowException_WhenOrderIsDelivered() {
            // given
            Pancake pancake = TestSamples.pancake();
            Order order = TestSamples.deliveredOrder();

            // when
            Exception exception = catchException(() -> order.removePancake(pancake, 1));

            // then
            assertThat(exception)
                    .isInstanceOf(OrderDeliveredException.class)
                    .hasMessageContaining("Cannot remove pancakes from a delivered order");
        }

        @RepeatedTest(10)
        void ShouldBeThreadSafe() throws InterruptedException {
            // given
            Order order = TestSamples.newEmptyOrder();
            Pancake pancake = TestSamples.pancake();
            order.addPancake(pancake, 101);

            int nThreads = 100;
            ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
            CyclicBarrier startingPoint = new CyclicBarrier(nThreads);

            // when
            IntStream.range(0, nThreads).forEach((i) -> executorService.execute(() -> {
                await(startingPoint);
                order.removePancake(pancake, 1);
            }));

            // then
            awaitTermination(executorService);

            assertThat(order.getPancakes()).containsExactly(Map.entry(pancake, 1));
        }
    }

    @Nested
    class MarkCancelled {

        @Test
        void ShouldMarkOrderAsCancelled_WhenOrderIsEmpty() {
            // given
            Order order = TestSamples.newEmptyOrder();

            // when
            order.markCancelled();

            // then
            assertThat(order.getOrderProcessingState()).isEqualTo(OrderProcessingState.CANCELLED);
        }

        @Test
        void ShouldMarkOrderAsCancelled_WhenOrderIsNotEmpty() {
            // given
            Order order = TestSamples.newOrderWithPancake();

            // when
            order.markCancelled();

            // then
            assertThat(order.getOrderProcessingState()).isEqualTo(OrderProcessingState.CANCELLED);
        }

        @Test
        void ShouldThrowException_WhenOrderIsCompleted() {
            // given
            Order order = TestSamples.completedOrder();

            // when
            Exception exception = catchException(order::markCancelled);

            // then
            assertThat(exception)
                    .isInstanceOf(OrderCompletedException.class)
                    .hasMessageContaining("Cannot cancel a completed order");
        }

        @Test
        void ShouldThrowException_WhenOrderIsPrepared() {
            // given
            Order order = TestSamples.preparedOrder();

            // when
            Exception exception = catchException(order::markCancelled);

            // then
            assertThat(exception)
                    .isInstanceOf(OrderPreparedException.class)
                    .hasMessageContaining("Cannot cancel a prepared order");
        }

        @Test
        void ShouldThrowException_WhenOrderIsDelivered() {
            // given
            Order order = TestSamples.deliveredOrder();

            // when
            Exception exception = catchException(order::markCancelled);

            // then
            assertThat(exception)
                    .isInstanceOf(OrderDeliveredException.class)
                    .hasMessageContaining("Cannot cancel a delivered order");
        }
    }

    @Nested
    class MarkCompleted {

        @Test
        void ShouldMarkOrderAsCompleted_WhenOrderHasPancakes() {
            // given
            Order order = TestSamples.newOrderWithPancake();

            // when
            order.markCompleted();

            // then
            assertThat(order.getOrderProcessingState()).isEqualTo(OrderProcessingState.COMPLETED);
        }

        @Test
        void ShouldThrowException_WhenCompletingEmptyOrder() {
            // given
            Order order = TestSamples.newEmptyOrder();

            // when
            Exception exception = catchException(order::markCompleted);

            // then
            assertThat(exception)
                    .isInstanceOf(OrderStateException.class)
                    .hasMessageContaining("Cannot complete an order with no pancakes");
        }

        @Test
        void ShouldThrowException_WhenOrderIsCancelled() {
            // given
            Order order = TestSamples.cancelledOrder();

            // when
            Exception exception = catchException(order::markCompleted);

            // then
            assertThat(exception)
                    .isInstanceOf(OrderCancelledException.class)
                    .hasMessageContaining("Cannot complete a cancelled order");
        }

        @Test
        void ShouldThrowException_WhenOrderIsPrepared() {
            // given
            Order order = TestSamples.preparedOrder();

            // when
            Exception exception = catchException(order::markCompleted);

            // then
            assertThat(exception)
                    .isInstanceOf(OrderPreparedException.class)
                    .hasMessageContaining("Cannot complete a prepared order");
        }

        @Test
        void ShouldThrowException_WhenOrderIsDelivered() {
            // given
            Order order = TestSamples.deliveredOrder();

            // when
            Exception exception = catchException(order::markCompleted);

            // then
            assertThat(exception)
                    .isInstanceOf(OrderDeliveredException.class)
                    .hasMessageContaining("Cannot complete a delivered order");
        }
    }

    @Nested
    class MarkPrepared {

        @Test
        void ShouldMarkOrderAsPrepared_WhenOrderIsCompleted() {
            // given
            Order order = TestSamples.completedOrder();

            // when
            order.markPrepared();

            // then
            assertThat(order.getOrderProcessingState()).isEqualTo(OrderProcessingState.PREPARED);
        }

        @Test
        void ShouldThrowException_WhenOrderIsNew() {
            // given
            Order order = TestSamples.newOrderWithPancake();

            // when
            Exception exception = catchException(order::markPrepared);

            // then
            assertThat(exception)
                    .isInstanceOf(NewOrderException.class)
                    .hasMessageContaining("Cannot prepare a new order. Order must be completed first.");
        }

        @Test
        void ShouldThrowException_WhenOrderIsCancelled() {
            // given
            Order order = TestSamples.cancelledOrder();

            // when
            Exception exception = catchException(order::markPrepared);

            // then
            assertThat(exception)
                    .isInstanceOf(OrderCancelledException.class)
                    .hasMessageContaining("Cannot prepare a cancelled order");
        }

        @Test
        void ShouldThrowException_WhenOrderIsDelivered() {
            // given
            Order order = TestSamples.deliveredOrder();

            // when
            Exception exception = catchException(order::markPrepared);

            // then
            assertThat(exception)
                    .isInstanceOf(OrderDeliveredException.class)
                    .hasMessageContaining("Cannot prepare a delivered order");
        }
    }

    @Nested
    class MarkDelivered {

        @Test
        void ShouldMarkOrderAsDelivered_WhenOrderIsPrepared() {
            // given
            Order order = TestSamples.preparedOrder();

            // when
            order.markDelivered();

            // then
            assertThat(order.getOrderProcessingState()).isEqualTo(OrderProcessingState.DELIVERED);
        }

        @Test
        void ShouldThrowException_WhenOrderIsCompleted() {
            // given
            Order order = TestSamples.completedOrder();

            // when
            Exception exception = catchException(order::markDelivered);

            // then
            assertThat(exception)
                    .isInstanceOf(OrderCompletedException.class)
                    .hasMessageContaining("Cannot deliver a completed order. Order must be prepared first.");
        }

        @Test
        void ShouldThrowException_WhenOrderIsCancelled() {
            // given
            Order order = TestSamples.cancelledOrder();

            // when
            Exception exception = catchException(order::markDelivered);

            // then
            assertThat(exception)
                    .isInstanceOf(OrderCancelledException.class)
                    .hasMessageContaining("Cannot deliver a cancelled order");
        }
    }

    private static void await(CyclicBarrier startingPoint) {
        try {
            startingPoint.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    private static void awaitTermination(ExecutorService executorService) throws InterruptedException {
        executorService.shutdown();
        if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
            fail("Something went wrong");
        }
    }
}