package OrphanRate;

import Blockchain.Network;
import Blockchain.Util.Logger;
import DoubleSpend.Parameters;
import java.util.logging.Level;

public class ORManager {
    //Length of longest chain
    private int chainLength;
    
    //Number of Orphans created by the network
    private int orphans;
    
    private Parameters p;
    private ORSimulation sim;
    private Network network;
    
    public ORManager(Parameters p, ORSimulation sim, Network network){
        this.p = p;
        this.sim = sim;
        this.network = network;
        this.chainLength = 0;
        this.orphans = 0;
    }

    public synchronized void registerChain(int length) {
        if(network.stopped())
            return;
        if(chainLength >= length){
            orphans++;
            return;
        }
        chainLength = length;
        
        Logger.log(Level.FINE, String.format("Chain: %d",length));
        
        if(chainLength >= p.getMaxLength()) {
            sim.report(chainLength, orphans);
        }
    }

}
