package DoubleSpend;

import Blockchain.Network;
import Blockchain.Node;
import Blockchain.Peers.PeerStrategy;
import Blockchain.Util.Logger;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Simulation of a Network of Trusted and Attacking Nodes, where Attacking Nodes
 * are trying to achive double spends by indroducing malicious transaktions and 
 * mining on private forks of the Blockchain.
 */
public class DSSimulation {
    private Parameters p;
    private Network network;
    
    //Number of (un-)successful Double Spend attempts
    private volatile int success;
    private volatile int failure;
    
    //Overall mined blocks and stale blocks by trusted and attacker network
    private volatile int aBlocks, tBlocks;
    private volatile int aStaleBlocks, tStaleBlocks;
    
    private PeerStrategy trustedPeerStrat;
    private PeerStrategy attackerPeerStrat;
    private ConnectionStrategy attackerStrat;
    
    private ArrayList<Node> trustedNodes;
    private ArrayList<Node> attackerNodes;
    private ArrayList<Node> nodes;
    
    public DSSimulation(Parameters p) {
        this(p, p.getTrustedPeerStrategy(), p.getAttackerPeerStrategy(), p.getConnectionStrategy());
    }
    
    /**
     * Creates a new Simulation for double spends.
     * @param p The Parameters of this Simulation
     * @param trustedPeerStrat The PeerStrategy of creating the trusted network
     * @param attackerPeerStrat The PeerStrategy of creating the attacker network
     * @param attackerStrat The Strategy of connecting the attacker network with the trusted network
     */
    public DSSimulation(Parameters p, PeerStrategy trustedPeerStrat, 
            PeerStrategy attackerPeerStrat, ConnectionStrategy attackerStrat) {
        Logger.setLevel(p.getLogLevel());
        this.p = p;
        this.network = new Network();
        this.success = this.failure = 0;
        
        this.trustedPeerStrat = trustedPeerStrat;
        this.attackerPeerStrat = attackerPeerStrat;
        this.attackerStrat = attackerStrat;
        
        this.trustedNodes = new ArrayList<>(p.getTrustedNodes());
        this.attackerNodes = new ArrayList<>(p.getAttackerNodes());
        this.nodes = new ArrayList<>(p.getNodes());
                
        DSManager dsm = new DSManager(p, this, network);
        for (int i = 0; i < p.getTrustedNodes(); i++) {
            trustedNodes.add(new TrustedNode(dsm, network, p, "Trusted "+i));
        }
        for (int i = p.getTrustedNodes(); i < p.getNodes(); i++) {
            attackerNodes.add(new AttackerNode(dsm, network, p, "Attacker "+(i-p.getTrustedNodes())));
        }
        nodes.addAll(trustedNodes);
        nodes.addAll(attackerNodes);

        network.setNodes(nodes);
    }
    
    private void createPeers() {
        long maxTrustedLatency = trustedPeerStrat.connectPeers(trustedNodes);
        long maxAttackerLatency = attackerPeerStrat.connectPeers(attackerNodes);
        long maxLatency = Math.max(maxTrustedLatency, maxAttackerLatency);
        long maxConnLatency = attackerStrat.connectPeers(attackerNodes, trustedNodes);
        maxLatency = Math.max(maxLatency, maxConnLatency);
        network.setMaxLatency(maxLatency);
    }

    /**
     * Starts this Simulation.
     */  
    public void start() {
        while(success+failure < p.getRuns()){
            createPeers();
            p.getConfirmationsIntParameter().next();
            network.run();
        }
        
        Logger.log(Level.INFO, String.format(
                "Successful Double Spends: %d\n"
                + "Ratio of trusted stale blocks: %s\n"
                + "Ratio of attacker stale blocks: %s",
                success, ""+((double)tStaleBlocks)/tBlocks, ""+((double)aStaleBlocks)/aBlocks
        ));
    }
    
    /**
     * Called after (un-)successful Double Spend attempt. Stops the network so
     * a new run of the Simulation can be started.
     * @param successful Wether the Double Spend attempt was successful
     * @param attackerChain The final length of the attacker fork of the Blockchain
     * @param trustedChain The final length of the Blockchain created by the Network of trusted Nodes
     * @param aSB The number of stale blocks mined by the attacking Network
     * @param tSB The number of stale blocks mined by the trusted Network
     */
    public void report(boolean successful, int attackerChain, int trustedChain, 
            int aSB, int tSB) {
        
        if(successful){
            success++;
            Logger.log(Level.FINE, String.format("%d: SUCCESS t:%d a:%d", 
                    success+failure,trustedChain,attackerChain));
        }else{
            failure++;
            Logger.log(Level.FINE, String.format("%d: FAILURE t:%d a:%d", 
                    success+failure,trustedChain,attackerChain));
        }
        
        aBlocks += attackerChain+aSB;
        tBlocks += trustedChain+tSB;
        aStaleBlocks += aSB;
        tStaleBlocks += tSB;
        
        network.stop();
    }
}
