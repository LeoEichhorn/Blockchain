package Blockchain.Util;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierUtil {
 
    /**
     * http://shanhe.me/2013/06/20/breaking-a-thread-coordinating-cyclic-barrier
     * @param barrier
     */
    public static void breakCyclicBarrier(final CyclicBarrier barrier) {
    if(Thread.currentThread().isInterrupted()) {
      try {
        barrier.await();
      } catch(final BrokenBarrierException | InterruptedException unused) {
        Thread.currentThread().interrupt();
      }
    } else {
      Thread.currentThread().interrupt();
      try {
        barrier.await();
      } catch(final BrokenBarrierException | InterruptedException unused) {
        Thread.interrupted();
      }
    }
  }
}