package org.project.producerconsumer.producer;

import org.project.producerconsumer.common.BoundedBlockingQueue;

import java.util.List;

/**
 * Producer runnable that reads elements from a source container and
 * publishes them into a shared {@link BoundedBlockingQueue}. Once all
 * data has been produced, it sends a single "poison pill" value to
 * signal completion to the consumer.
 */
public class Producer implements Runnable {

    private final List<Integer> source;
    private final BoundedBlockingQueue<Integer> queue;
    private final int poisonPill;

    /**
     * Creates a new producer.
     *
     * @param source     list from which items will be read
     * @param queue      shared blocking queue to which items are written
     * @param poisonPill special marker value sent after all real items
     */
    public Producer(List<Integer> source,
                    BoundedBlockingQueue<Integer> queue,
                    int poisonPill) {
        this.source = source;
        this.queue = queue;
        this.poisonPill = poisonPill;
    }

    @Override
    public void run() {
        try {
            // Publish every element from the source container to the queue.
            for (Integer item : source) {
                System.out.printf("[%s] Producing: %d%n",
                        Thread.currentThread().getName(), item);
                queue.put(item);
            }

            // Finally send the poison pill to let the consumer know that
            // no more real items will be produced.
            System.out.printf("[%s] Producing poison pill: %d%n",
                    Thread.currentThread().getName(), poisonPill);
            queue.put(poisonPill);
        } catch (InterruptedException e) {
            // Restore the interrupt status and log the interruption.
            Thread.currentThread().interrupt();
            System.err.printf("[%s] Producer interrupted%n",
                    Thread.currentThread().getName());
        }
    }
}
