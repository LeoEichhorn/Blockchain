package Blockchain;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Network {
    private Parameters p;
    private ArrayList<Node> nodes;
    private long maxLatency;
    private volatile boolean stopped;
    
    public Network(Parameters p){
        this.p = p;
        this.stopped = true;
    }
    
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
