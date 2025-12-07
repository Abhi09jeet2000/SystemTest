package org.project.producerconsumer;

import org.junit.jupiter.api.Test;
import org.project.producerconsumer.common.BoundedBlockingQueue;
import org.project.producerconsumer.common.Constants;
import org.project.producerconsumer.consumer.Consumer;
import org.project.producerconsumer.producer.Producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the producer-consumer implementation and the underlying
 * {@link BoundedBlockingQueue}. These tests go beyond the "happy path"
 * to exercise edge cases and basic blocking behavior.
 */
class ProducerConsumerTest {

    @Test
    void producerConsumerTransfersAllItems() throws InterruptedException {
        List<Integer> source = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            source.add(i);
        }

        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(5);
        List<Integer> destination =
                Collections.synchronizedList(new ArrayList<>());

        Producer producer = new Producer(source, queue, Constants.POISON_PILL);
        Consumer consumer = new Consumer(queue, destination, Constants.POISON_PILL);

        Thread producerThread = new Thread(producer, "Producer-Test");
        Thread consumerThread = new Thread(consumer, "Consumer-Test");

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();

        // All items must have been transferred from source to destination
        assertEquals(source.size(), destination.size());
        assertTrue(destination.containsAll(source));

        // The poison pill is a control marker and must NOT end up in the destination.
        assertFalse(destination.contains(Constants.POISON_PILL));
    }

    @Test
    void emptySourceResultsInEmptyDestination() throws InterruptedException {
        List<Integer> source = new ArrayList<>();

        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(3);
        List<Integer> destination =
                Collections.synchronizedList(new ArrayList<>());

        Producer producer = new Producer(source, queue, Constants.POISON_PILL);
        Consumer consumer = new Consumer(queue, destination, Constants.POISON_PILL);

        Thread producerThread = new Thread(producer, "Producer-Empty-Source");
        Thread consumerThread = new Thread(consumer, "Consumer-Empty-Source");

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();

        assertTrue(destination.isEmpty(),
                "Destination should remain empty when source is empty");
    }

    @Test
    void boundedQueueRejectsNonPositiveCapacity() {
        assertThrows(IllegalArgumentException.class,
                () -> new BoundedBlockingQueue<>(0));
        assertThrows(IllegalArgumentException.class,
                () -> new BoundedBlockingQueue<>(-10));
    }

    @Test
    void boundedQueuePreservesFifoOrder() throws InterruptedException {
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(3);

        queue.put(1);
        queue.put(2);
        queue.put(3);

        assertEquals(1, queue.take());
        assertEquals(2, queue.take());
        assertEquals(3, queue.take());
        assertEquals(0, queue.size());
    }

    @Test
    void putBlocksWhenQueueIsFullAndUnblocksWhenSpaceAvailable() throws InterruptedException {
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(1);

        // Fill the queue to capacity.
        queue.put(1);

        Thread producerThread = new Thread(() -> {
            try {
                queue.put(2); // this call should block until an element is taken
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Blocking-Producer");

        producerThread.start();

        // Give the producer a brief moment to attempt the put and block.
        Thread.sleep(100);

        assertTrue(producerThread.isAlive(),
                "Producer thread should be blocked when queue is full");

        // Make room in the queue; this should unblock the producer.
        int taken = queue.take();
        assertEquals(1, taken);

        // The producer should now complete within a reasonable timeout.
        producerThread.join(1_000);
        assertFalse(producerThread.isAlive(),
                "Producer thread should finish after space becomes available");

        assertEquals(1, queue.size(),
                "Exactly one element (2) should remain in the queue after unblocking");
    }

    @Test
    void takeBlocksWhenQueueIsEmptyAndUnblocksWhenItemArrives() throws InterruptedException {
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(1);
        List<Integer> consumed = Collections.synchronizedList(new ArrayList<>());

        Thread consumerThread = new Thread(() -> {
            try {
                int value = queue.take(); // should block until an element is put
                consumed.add(value);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Blocking-Consumer");

        consumerThread.start();

        // Give the consumer a brief moment to attempt the take and block.
        Thread.sleep(100);

        assertTrue(consumerThread.isAlive(),
                "Consumer thread should be blocked when queue is empty");

        // Now put a value, which should unblock the consumer.
        queue.put(42);

        consumerThread.join(1_000);
        assertFalse(consumerThread.isAlive(),
                "Consumer thread should finish after an element is put");

        assertEquals(1, consumed.size());
        assertEquals(42, consumed.get(0));
    }
}
