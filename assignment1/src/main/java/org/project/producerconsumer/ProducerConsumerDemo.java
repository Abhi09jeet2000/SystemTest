package org.project.producerconsumer;

import org.project.producerconsumer.common.BoundedBlockingQueue;
import org.project.producerconsumer.common.Constants;
import org.project.producerconsumer.consumer.Consumer;
import org.project.producerconsumer.producer.Producer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple demo wiring together a {@link Producer} and {@link Consumer}
 * using a {@link BoundedBlockingQueue}. This class is not used by the
 * tests directly, but serves as an executable example of the classic
 * producer-consumer pattern with wait/notify based synchronization.
 */
public class ProducerConsumerDemo {

    public static void main(String[] args) throws InterruptedException {
        // Source container containing the data that must be transferred.
        List<Integer> source = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            source.add(i);
        }

        // Shared bounded blocking queue through which the producer and
        // consumer communicate.
        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(5);

        // Destination container that will collect the consumed elements.
        // We wrap the list with Collections.synchronizedList so that it is
        // safe to use from multiple threads if needed.
        List<Integer> destination =
                Collections.synchronizedList(new ArrayList<>());

        Producer producer = new Producer(source, queue, Constants.POISON_PILL);
        Consumer consumer = new Consumer(queue, destination, Constants.POISON_PILL);

        Thread producerThread = new Thread(producer, "Producer-Thread");
        Thread consumerThread = new Thread(consumer, "Consumer-Thread");

        producerThread.start();
        consumerThread.start();

        // Wait for the threads to finish so we can safely inspect results.
        producerThread.join();
        consumerThread.join();

        System.out.println("\n=== Final Results ===");
        System.out.println("Source:      " + source);
        System.out.println("Destination: " + destination);
    }
}
