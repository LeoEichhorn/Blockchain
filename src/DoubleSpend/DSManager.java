package DoubleSpend;

import Blockchain.Network;
import Blockchain.Util.Logger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * Collects data about total blocks mined and number of stale block.
 * Reports results to Simulation.
 */
public class DSManager {
    //Length of longest trusted chain
    private int maxTrustedChain;
    //Length of longest attacker chain
    private int maxAttackerChain;
    
    //Number of stale blocks created by the trusted network
    private int tStaleBlocks;
    //Number of stale blocks created by the attacker network
    private int aStaleBlocks;
    
    //Number of Nodes convinced by the double-spending transaction
    private AtomicInteger convinced;

    private Parameters p;
    private DSSimulation sim;
    private Network network;
    
    public DSManager(Parameters p, DSSimulation sim, Network network){
        this.p = p;
        this.sim = sim;
        this.network = network;
        this.maxAttackerChain = 0;
        this.maxTrustedChain  = 0;
        this.tStaleBlocks = 0;
        this.aStaleBlocks = 0;
        this.convinced = new AtomicInteger();
    }
    
    /**
     * Called once a trusted Node is newly convinced of the double-spending transaction. 
     * A Node is considered to be convinced once it mines on a Blockchain containing the malicous transaction.
     * The double spend attempt is considered succesful if all trusted Nodes have been convinced.
     */
    public void addConvinced(){
        if(convinced.incrementAndGet() == p.getTrustedNodes()){
            synchronized (this) {
                sim.report(true, maxAttackerChain, maxTrustedChain, aStaleBlocks, tStaleBlocks);
                reset();
            }
        }
    }
    
    /**
     * Called once a trusted Node is no longer convinced of the double-spending transaction.
     */
    public void removeConvinced(){
        convinced.decrementAndGet();
    }
    
    /**
     * Newly found Blocks are registered to keep track of 
     * the longest chain and number of stale blocks in the Network.
     * @param chainLength The length of the new Blockchain
     */
    public synchronized void registerTrustedChain(int chainLength){
        if(network.stopped())
            return;
        if(maxTrustedChain >= chainLength){
            tStaleBlocks++;
            return;
        }
        maxTrustedChain = chainLength;
        Logger.log(Level.FINER, String.format("Trusted chain: %d",maxTrustedChain));
        checkFailure();
    }
    
    /**
     * Newly found Blocks are registered to keep track of 
     * the longest chain and number of stale blocks in the Network.
     * @param chainLength The length of the new Blockchain
     */
    public synchronized void registerAttackerChain(int chainLength){
        if(network.stopped())
            return;
        if(maxAttackerChain >= chainLength){
            aStaleBlocks++;
            return;
        }
        maxAttackerChain = chainLength;
        Logger.log(Level.FINER, String.format("Attacker chain: %d",maxAttackerChain));
    }
    
    /**
    * Checks if the current double spend attempt should be aborted. 
    * Reports result to Simulation.
    */
    private void checkFailure(){   
        //current attempt will be aborted if attackers are falling too far behind,
        //or a maximum blockchain length has been reached
        if(p.getMaxLead() < maxTrustedChain - maxAttackerChain || Math.max(maxAttackerChain, maxTrustedChain) > p.getMaxLength()){  
            sim.report(false, maxAttackerChain, maxTrustedChain, aStaleBlocks, tStaleBlocks);
            reset();
        }
    }
    
    private void reset() {
        convinced = new AtomicInteger();
        maxAttackerChain = maxTrustedChain = 0;
        aStaleBlocks = tStaleBlocks = 0;
    }
}
