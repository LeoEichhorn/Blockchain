package StaleBlocks;

import Blockchain.Network;
import Blockchain.Node;
import DoubleSpend.Parameters;
import Blockchain.Peers.PeerStrategy;
import Blockchain.Util.Logger;
import java.util.ArrayList;
import java.util.logging.Level;

public class SBSimulation {
    private Network network;
    private SBManager orm;
    
    //Overall mined blocks and stale blocks by network
    private int blocks;
    private int staleBlocks;
    
    private PeerStrategy peerStrategy;
    
    private ArrayList<Node> nodes;
    
    /**
     * Creates a new Simulation for stale block rates.
     * @param p The Parameters of this Simulation
     * @param peerStrategy The Strategy of creating the network
     */
    public SBSimulation(Parameters p, PeerStrategy peerStrategy) {
        
        this.network = new Network();
        this.orm = new SBManager(p, this, network);
        
        this.peerStrategy = peerStrategy;
        this.nodes = new ArrayList<>(p.getNodes());

        for (int i = 0; i < p.getNodes(); i++) {
            nodes.add(new SBNode(orm, network, p, "Node "+i));
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
        
        Logger.log(Level.INFO, String.format(
                "Stale blocks: %d\n"
                + "Blocks: %d\n"
                + "Stale block Rate: %s",
                staleBlocks, blocks, ""+((double)staleBlocks)/blocks
        ));
    }
    
    /**
     * Called after (un-)successful Double Spend attempt. Stops the network so
     * a new run of the Simulation can be started.
     * @param chainLength
     * @param staleBlocks
     */
    public void report(int chainLength, int staleBlocks) {
        blocks = chainLength+staleBlocks;
        this.staleBlocks = staleBlocks;
        
        network.stop();
    }
}

