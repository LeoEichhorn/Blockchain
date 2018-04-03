package Blockchain;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Simulation {
    private Parameters p;
    private DoubleSpendManager dsm;
    private ArrayList<Node> nodes;
    private long maxLatency;
    private int success;
    private int failure;
    private volatile boolean stopped;

    public Simulation(Parameters p) {
        this.p = p;
        this.dsm = new DoubleSpendManager(p, this);
        this.maxLatency = 1000;
        this.success = this.failure = 0;
        
        this.nodes = new ArrayList<>(p.getNodes());
        ArrayList<Node> attackers = new ArrayList<>(p.getAttackerNodes());
        
        CyclicBarrier gate = new CyclicBarrier(p.getNodes());
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
        
        for (int i = 0; i < p.getTrustedNodes(); i++) {
            nodes.add(new TrustedNode(dsm, this, gate, p, executor, "Trusted "+i));
        }
        for (int i = p.getTrustedNodes(); i < p.getNodes(); i++) {
            attackers.add(new AttackerNode(dsm, this, gate, p, executor, "Attacker "+(i-p.getTrustedNodes())));
        }
        
        AbstractPeerFactory trustedPeerFactory = new ConstantPeerFactory(10);
        AbstractPeerFactory attackerPeerFactory = new ConstantPeerFactory(10);
        long maxTrustedLatency = trustedPeerFactory.createPeers(nodes);
        long maxAttackerLatency = attackerPeerFactory.createPeers(attackers);
        this.maxLatency = Math.max(maxTrustedLatency, maxAttackerLatency);
        
        nodes.addAll(attackers);
    }
    
    private void run() {
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
    
    public void start() {
        while(success+failure < p.getRuns()){
            run();
        }
        System.out.println("Successful Double Spends: "+success);
    }
    
    public void report(boolean successful, int attackerChain, int trustedChain) {
        if(successful){
            success++;
            if(p.getLogLevel().intValue() <= Level.FINE.intValue())
                System.out.println((success+failure)+": SUCCESS t:"+trustedChain+" a:"+attackerChain);
        }else{
            failure++;
            if(p.getLogLevel().intValue() <= Level.FINE.intValue())
                System.out.println((success+failure)+": FAILURE t:"+trustedChain+" a:"+attackerChain);
        }
        stopped = true;
    }
    
    public boolean stopped() {
        return stopped;
    }
}
