package OrphanRate;

import Blockchain.Network;
import Blockchain.Node;
import DoubleSpend.Parameters;
import Blockchain.Peers.PeerStrategy;
import java.util.ArrayList;

/**
 * Simulation of a Network of Trusted and Attacking Nodes, where Attacking Nodes
 * are trying to achive double spends by indroducing malicious transaktions and 
 * mining on private forks of the Blockchain.
 */
public class ORSimulation {
    private Parameters p;
    private Network network;
    private ORManager orm;
    
    //Overall mined Blocks and Orphans by network
    private int blocks;
    private int orphans;
    
    private PeerStrategy peerStrategy;
    
    private ArrayList<Node> nodes;
    
    /**
     * Creates a new Simulation for Orphan rates.
     * @param p The Parameters of this Simulation
     * @param peerStrategy The Strategy of creating the network
     */
    public ORSimulation(Parameters p, PeerStrategy peerStrategy) {
        
        this.p = p;
        this.network = new Network(p.getNodes());
        this.orm = new ORManager(p, this, network);
        
        this.peerStrategy = peerStrategy;
        this.nodes = new ArrayList<>(p.getNodes());

        for (int i = 0; i < p.getNodes(); i++) {
            nodes.add(new ORNode(orm, network, p, "Node "+i));
        }

        network.setNodes(nodes);
    }
    
    private void createPeers() {
        for(Node n : nodes)
            n.resetPeers();
        long maxLatency = peerStrategy.connectPeers(nodes);
        network.setMaxLatency(maxLatency);
    }

    /**
     * Starts this Simulation.
     */
    public void start() {
        createPeers();
        network.run();
        System.out.println("Orphans: "+orphans);
        System.out.println("Blocks: "+blocks);
        System.out.println("Orphan Rate: "+((double)orphans)/blocks);
    }
    
    /**
     * Called after (un-)successful Double Spend attempt. Stops the network so
     * a new run of the Simulation can be started.
     * @param chainLength
     * @param orphanedBlocks
     */
    public void report(int chainLength, int orphanedBlocks) {
        blocks = chainLength+orphanedBlocks;
        orphans = orphanedBlocks;
        
        network.stop();
    }
}

