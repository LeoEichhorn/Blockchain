package Blockchain;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ScheduledExecutorService;

public class TrustedNode extends Node{
    private DoubleSpendManager dsm;
    
    public TrustedNode(DoubleSpendManager dsm, Simulation sim, CyclicBarrier gate, 
            Parameters p, ScheduledExecutorService executor, String name) {
        super(sim, gate, p, executor, name);
        this.dsm = dsm;
    }
    
    @Override
    protected void registerBlockchain(int blockchain){
        dsm.registerTrustedChain(blockchain);
    }
}
