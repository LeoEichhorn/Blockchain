package Blockchain.Util;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierUtil {

    /**
     * All currently waiting Threads and future calls to await on this CyclicBarrier will return instantly.
     * Inspired by:
     * http://shanhe.me/2013/06/20/breaking-a-thread-coordinating-cyclic-barrier
     * @param barrier The CyclicBarrier to be broken
     */
    public static void breakCyclicBarrier(final CyclicBarrier barrier) {
        try {
            Thread.sleep(80);
        } catch (final InterruptedException unused) {}
        if (Thread.currentThread().isInterrupted()) {
            try {
                barrier.await();
            } catch (final BrokenBarrierException | InterruptedException unused) {
                Thread.currentThread().interrupt();
            }
        } else {
            Thread.currentThread().interrupt();
            try {
                barrier.await();
            } catch (final BrokenBarrierException | InterruptedException unused) {
                Thread.interrupted();
            }
        }
    }
}
