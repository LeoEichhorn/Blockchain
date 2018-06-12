package StaleBlocks;

import Blockchain.Network;
import Blockchain.Util.Logger;
import DoubleSpend.Parameters;
import java.util.logging.Level;

public class SBManager {
    //Length of longest chain
    private int chainLength;
    
    //Number of stale blocks created by the network
    private int staleBlocks;
    
    private Parameters p;
    private SBSimulation sim;
    private Network network;
    
    public SBManager(Parameters p, SBSimulation sim, Network network){
        this.p = p;
        this.sim = sim;
        this.network = network;
        this.chainLength = 0;
        this.staleBlocks = 0;
    }

    public synchronized void registerChain(int length) {
        if(network.stopped())
            return;
        if(chainLength >= length){
            staleBlocks++;
            return;
        }
        chainLength = length;
        
        Logger.log(Level.FINE, String.format("Chain: %d",length));
        
        if(chainLength >= p.getMaxLength()) {
            sim.report(chainLength, staleBlocks);
        }
    }

}
