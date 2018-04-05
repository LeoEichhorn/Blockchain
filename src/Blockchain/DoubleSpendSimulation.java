package Blockchain;

import Blockchain.DoubleSpend.*;
import Blockchain.Peers.PeerStrategy;
import java.util.ArrayList;
import java.util.logging.Level;

public class DoubleSpendSimulation {
    private Parameters p;
    private Network network;
    private DoubleSpendManager dsm;
    
    private int success;
    private int failure;
    
    private int aBlocks, tBlocks;
    private int aOrphans, tOrphans;
    
    public DoubleSpendSimulation(Parameters p, PeerStrategy trustedStrategy, PeerStrategy attackerStrategy) {
        
        this.p = p;
        this.network = new Network(p);
        this.dsm = new DoubleSpendManager(p, this, network);
        this.success = this.failure = 0;
        
        ArrayList<Node> nodes = new ArrayList<>(p.getNodes());
        ArrayList<Node> attackers = new ArrayList<>(p.getAttackerNodes());
        
        
        for (int i = 0; i < p.getTrustedNodes(); i++) {
            nodes.add(new TrustedNode(dsm, network, p, "Trusted "+i));
        }
        for (int i = p.getTrustedNodes(); i < p.getNodes(); i++) {
            attackers.add(new AttackerNode(dsm, network, p, "Attacker "+(i-p.getTrustedNodes())));
        }

        long maxTrustedLatency = trustedStrategy.connectPeers(nodes);
        long maxAttackerLatency = attackerStrategy.connectPeers(attackers);
        long maxLatency = Math.max(maxTrustedLatency, maxAttackerLatency);

        nodes.addAll(attackers);
        
        network.setNodes(nodes);
        network.setMaxLatency(maxLatency);
    }
    
    public void start() {
        while(success+failure < p.getRuns()){
            network.run();
        }
        System.out.println("Successful Double Spends: "+success);
        System.out.println("Trusted Orphan Rate: "+((double)tOrphans)/tBlocks);
        System.out.println("Attacker Orphan Rate: "+((double)aOrphans)/aBlocks);
    }
    
    public void report(boolean successful, int attackerChain, int trustedChain, 
            int attackerOrphans, int trustedOrphans) {
        
        if(successful){
            success++;
            if(p.getLogLevel().intValue() <= Level.FINE.intValue())
                System.out.println((success+failure)+": SUCCESS t:"+trustedChain+" a:"+attackerChain);
        }else{
            failure++;
            if(p.getLogLevel().intValue() <= Level.FINE.intValue())
                System.out.println((success+failure)+": FAILURE t:"+trustedChain+" a:"+attackerChain);
        }
        
        aBlocks += attackerChain+attackerOrphans;
        tBlocks += trustedChain+trustedOrphans;
        aOrphans += attackerOrphans;
        tOrphans += trustedOrphans;
        
        network.stop();
    }
}
