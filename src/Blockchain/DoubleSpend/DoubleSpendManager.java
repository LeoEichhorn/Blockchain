package Blockchain.DoubleSpend;

import Blockchain.Network;
import Blockchain.Parameters;
import Blockchain.DoubleSpendSimulation;
import java.util.logging.Level;

public class DoubleSpendManager {
    //length of trusted chain
    private int trusted;
    //length of attacker fork
    private int attacker;
    
    private int trustedOrphans;
    private int attackerOrphans;

    private Parameters p;
    private DoubleSpendSimulation sim;
    private Network network;
    
    public DoubleSpendManager(Parameters p, DoubleSpendSimulation sim, Network network){
        this.p = p;
        this.sim = sim;
        this.network = network;
        this.attacker = 0;
        this.trusted  = 0;
        this.trustedOrphans = 0;
        this.attackerOrphans = 0;
    }
    
    /*
    * Adding to a chain consists of incrementing the length of the
    * corresponding chain and checking for a successful double spend.
    * Check for stopped() is needed since there might still be nodes 
    * mining at the time all runs have been completed.
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
        checkSuccess();
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
        checkSuccess();
    }
    
    /*
    * Checks if double spend is possible or if the current 
    * attempt should be aborted
    */
    private void checkSuccess(){
        //Double spend happens if the first block is confirmed 
        //and the attacking chain is longer than the trusted chain
        if(trusted >= p.getConfirmations() && attacker > trusted){
            //success
            sim.report(true, attacker, trusted, attackerOrphans, trustedOrphans);
            attacker = trusted = 0;
            attackerOrphans = trustedOrphans = 0;
            
        //current attempt will be aborted if attackers are falling too far behind
        }else if(p.getMaxLead() < trusted - attacker || Math.max(attacker, trusted) > p.getMaxLength()){  
            //failure
            sim.report(false, attacker, trusted, attackerOrphans, trustedOrphans);
            attacker = trusted = 0;
            attackerOrphans = trustedOrphans = 0;
        }
    }
}
