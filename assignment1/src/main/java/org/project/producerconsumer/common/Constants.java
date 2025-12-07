package org.project.producerconsumer.common;

/**
 * Common constants shared between producer / consumer code.
 */
public final class Constants {

    private Constants() {
        // utility class; prevent instantiation
    }

    /**
     * Special marker value used to signal to the consumer that no more
     * real data will arrive and that it should terminate.
     */
    public static final int POISON_PILL = -1;
}
