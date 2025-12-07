package org.project.producerconsumer.consumer;

import org.project.producerconsumer.common.BoundedBlockingQueue;

import java.util.List;

/**
 * Consumer runnable that continuously reads items from a shared
 * {@link BoundedBlockingQueue} and stores them into a destination
 * container until it receives a poison pill value.
 */
public class Consumer implements Runnable {

    private final BoundedBlockingQueue<Integer> queue;
    private final List<Integer> destination;
    private final int poisonPill;

    /**
     * Creates a new consumer.
     *
     * @param queue       shared blocking queue from which items are read
     * @param destination list where consumed items are stored
     * @param poisonPill  marker value that signals termination
     */
    public Consumer(BoundedBlockingQueue<Integer> queue,
                    List<Integer> destination,
                    int poisonPill) {
        this.queue = queue;
        this.destination = destination;
        this.poisonPill = poisonPill;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int item = queue.take();

                // Check for poison pill and stop when it arrives.
                if (item == poisonPill) {
                    System.out.printf("[%s] Received poison pill: %d%n",
                            Thread.currentThread().getName(), item);
                    break;
                }

                System.out.printf("[%s] Consuming: %d%n",
                        Thread.currentThread().getName(), item);
                destination.add(item);
            }
        } catch (InterruptedException e) {
            // Restore the interrupt status and log the interruption.
            Thread.currentThread().interrupt();
            System.err.printf("[%s] Consumer interrupted%n",
                    Thread.currentThread().getName());
        }
    }
}
