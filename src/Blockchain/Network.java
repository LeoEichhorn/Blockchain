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
    private Parameters p;
    
    //All nodes in the network
    private ArrayList<Node> nodes;
    
    //Maximum latency between any two peers
    private long maxLatency;
    
    //Network status
    private volatile boolean stopped;
    
    public Network(Parameters p){
        this.p = p;
        this.stopped = true;
        this.nodes = new ArrayList<>();
        this.maxLatency = 5000;
    }
    
    /**
     * All nodes in the network are reset and start to mine on a new blockchain.
     * Calling thread is blocked until network is stopped.
     */
    public void run() {
        stopped = false;
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
        CyclicBarrier gate = new CyclicBarrier(p.getNodes());
        
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
        this.maxLatency = maxLatency;
    }
    
    public void stop() {
        this.stopped = true;
    }
    
    public boolean stopped() {
        return stopped;
    }
}
