package Blockchain;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Controls execution of Nodes in the Network and their synchronization.
 */
public class Network {
    private int nodeCount;
    
    //All nodes in the Network
    private ArrayList<Node> nodes;
    
    //Maximum latency between any two peers
    private long maxLatency;
    
    //Network status
    private volatile boolean stopped;
    
    public Network(int nodeCount){
        this.nodeCount = nodeCount;
        this.stopped = true;
        this.nodes = new ArrayList<>();
        this.maxLatency = 5000;
    }
    
    /**
     * Maximum latency and Nodes in the Network should be set first.
     * All Nodes in the Network are reset and start to mine on a new Blockchain.
     * Calling thread is blocked until Network is stopped.
     */
    public void run() {
        stopped = false;
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
        CyclicBarrier gate = new CyclicBarrier(nodeCount);
        
        for(Node n : nodes) {
            n.reset(gate, executor);
        }
        for (Node n : nodes) {
            n.start();
        }
        for (Node n : nodes) {
            n.join();
        }
        
        executor.shutdownNow();
        try {
            executor.awaitTermination(maxLatency, TimeUnit.MILLISECONDS);
        }catch (InterruptedException e){}
    }
    
    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }
    
    public void setMaxLatency(long maxLatency) {
        this.maxLatency = Math.max(maxLatency, 200);
    }
    
    /**
     * Stops this Network's execution.
     * Calling thread of run() will return after Network is stopped.
     */
    public void stop() {
        this.stopped = true;
    }
    
    public boolean stopped() {
        return stopped;
    }
}
