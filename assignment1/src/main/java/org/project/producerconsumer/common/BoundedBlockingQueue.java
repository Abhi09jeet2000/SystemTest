package org.project.producerconsumer.common;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A simple bounded blocking queue implementation using Java's intrinsic
 * monitor lock and the wait/notifyAll mechanism.
 * <p>
 * The queue has a fixed {@code capacity}. Calls to {@link #put(Object)}
 * will block when the queue is full, and calls to {@link #take()} will
 * block when the queue is empty.
 *
 * @param <T> type of element stored in the queue
 */
public class BoundedBlockingQueue<T> {

    private final Queue<T> queue;
    private final int capacity;

    /**
     * Creates a new queue with the given positive capacity.
     *
     * @param capacity maximum number of elements that can be stored
     * @throws IllegalArgumentException if {@code capacity <= 0}
     */
    public BoundedBlockingQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    /**
     * Inserts the given element into the queue, blocking if the queue
     * is currently full.
     *
     * @param item element to insert
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public synchronized void put(T item) throws InterruptedException {
        // Use a while loop (and not if) to correctly handle spurious
        // wake-ups and re-check the condition after being notified.
        while (queue.size() == capacity) {
            wait();
        }
        queue.add(item);
        // Notify any waiting consumer threads that a new element is available.
        notifyAll();
    }

    /**
     * Removes and returns the head element of the queue, blocking if
     * the queue is currently empty.
     *
     * @return the removed element
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public synchronized T take() throws InterruptedException {
        // Again, we use a while loop so that the condition is re-checked
        // after each wake-up.
        while (queue.isEmpty()) {
            wait();
        }
        T item = queue.remove();
        // Notify any waiting producer threads that space is now available.
        notifyAll();
        return item;
    }

    /**
     * Returns the current number of elements in the queue.
     *
     * @return the number of elements currently stored
     */
    public synchronized int size() {
        return queue.size();
    }

    /**
     * Returns the maximum capacity of this queue.
     *
     * @return capacity configured at construction time
     */
    public int getCapacity() {
        return capacity;
    }
}
