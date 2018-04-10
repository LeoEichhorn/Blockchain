package DoubleSpend;

import Blockchain.Network;
import Blockchain.Parameters;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * Collects data about total blocks mined and orphan rates.
 * Reports results to Simulation.
 */
public class DSManager {
    //Length of longest trusted chain
    private int trusted;
    //Length of longest attacker chain
    private int attacker;
    
    //Number of Orphans created by the trusted network
    private int trustedOrphans;
    //Number of Orphans created by the attacker network
    private int attackerOrphans;
    
    //Number of infested Nodes in the Network
    private AtomicInteger infested;

    private Parameters p;
    private DSSimulation sim;
    private Network network;
    
    public DSManager(Parameters p, DSSimulation sim, Network network){
        this.p = p;
        this.sim = sim;
        this.network = network;
        this.attacker = 0;
        this.trusted  = 0;
        this.trustedOrphans = 0;
        this.attackerOrphans = 0;
        this.infested = new AtomicInteger();
    }
    
    /**
     * Called once a trusted Node is newly infested. A Node is considered infested
     * if it mines on a infested Blockchain.
     */
    public void addInfested(){
        if(infested.incrementAndGet() == p.getTrustedNodes()){
            synchronized (this) {
                sim.report(true, attacker, trusted, attackerOrphans, trustedOrphans);
                reset();
            }
        }
    }
    
    /**
     * Called once a trusted Node is no longer infested.
     */
    public void removeInfested(){
        infested.decrementAndGet();
    }
    
    /**
     * Newly found Blocks are registered to keep track of 
     * the longest chain and orphan rates in the Network.
     * @param newChain The length of the new Blockchain
     */
    public synchronized void registerTrustedChain(int newChain){
        if(network.stopped())
            return;
        if(trusted >= newChain){
            trustedOrphans++;
            return;
        }
        trusted = newChain;
        if(p.getLogLevel().intValue() <= Level.FINER.intValue())
            System.out.println("Trusted chain: "+trusted);
        checkFailure();
    }
    
    public synchronized void registerAttackerChain(int newChain){
        if(network.stopped())
            return;
        if(attacker >= newChain){
            attackerOrphans++;
            return;
        }
        attacker = newChain;
        if(p.getLogLevel().intValue() <= Level.FINER.intValue())
            System.out.println("Attacker chain: "+attacker);
    }
    
    /**
    * Checks if the current double spend attempt should be aborted. 
    * Reports result to Simulation.
    */
    private void checkFailure(){   
        //current attempt will be aborted if attackers are falling too far behind
        if(p.getMaxLead() < trusted - attacker || Math.max(attacker, trusted) > p.getMaxLength()){  
            sim.report(false, attacker, trusted, attackerOrphans, trustedOrphans);
            reset();
        }
    }
    
    private void reset() {
        infested = new AtomicInteger();
        attacker = trusted = 0;
        attackerOrphans = trustedOrphans = 0;
    }
}
