package DoubleSpend;

import Blockchain.Network;
import Blockchain.Node;
import Blockchain.Peers.PeerStrategy;
import Blockchain.Peers.RndGraphPeerStrategy;
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
    private DSManager dsm;
    
    //Number of (un-)successful Double Spend attempts
    private volatile int success;
    private volatile int failure;
    
    //Overall mined Blocks and Orphans by trusted and attacker network
    private volatile int aBlocks, tBlocks;
    private volatile int aOrphans, tOrphans;
    
    private PeerStrategy trustedPeerStrategy;
    private PeerStrategy attackerPeerStrategy;
    private ConnectionStrategy attackerStrategy;
    
    private ArrayList<Node> trustedNodes;
    private ArrayList<Node> attackerNodes;
    private ArrayList<Node> nodes;
    
    private boolean resetPeers;
    
    public DSSimulation(Parameters p, boolean resetPeers) {
        this(p,
            new RndGraphPeerStrategy(p.getTrustedNodes(), p.getTrustedGraphDensityParameter(), p.getTrustedLatencyParameter(), 0.1),
            new RndGraphPeerStrategy(p.getAttackerNodes(), p.getAttackerGraphDensityParameter(), p.getAttackerLatencyParameter(), 0.1),
            new ConstantConnectionStrategy(p.getConnectionLatencyParameter(), 0.1),
            resetPeers
        );
    }
    
    /**
     * Creates a new Simulation for double spends.
     * @param p The Parameters of this Simulation
     * @param trustedPeerStrategy The PeerStrategy of creating the trusted network
     * @param attackerPeerStrategy The PeerStrategy of creating the attacker network
     * @param attackerStrategy The Strategy of connecting the attacker network with the trusted network
     * @param resetPeers True - Peer network should be reset after each run
     */
    public DSSimulation(Parameters p, PeerStrategy trustedPeerStrategy, 
            PeerStrategy attackerPeerStrategy, ConnectionStrategy attackerStrategy, boolean resetPeers) {
        
        this.p = p;
        this.network = new Network(p.getNodes());
        this.dsm = new DSManager(p, this, network);
        this.success = this.failure = 0;
        
        this.trustedPeerStrategy = trustedPeerStrategy;
        this.attackerPeerStrategy = attackerPeerStrategy;
        this.attackerStrategy = attackerStrategy;
        
        this.trustedNodes = new ArrayList<>(p.getTrustedNodes());
        this.attackerNodes = new ArrayList<>(p.getAttackerNodes());
        this.nodes = new ArrayList<>(p.getNodes());
        
        this.resetPeers = resetPeers;
        
        
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
        for(Node n : nodes)
            n.resetPeers();
        long maxTrustedLatency = trustedPeerStrategy.connectPeers(trustedNodes);
        long maxAttackerLatency = attackerPeerStrategy.connectPeers(attackerNodes);
        long maxLatency = Math.max(maxTrustedLatency, maxAttackerLatency);
        long maxConnLatency = attackerStrategy.connectPeers(attackerNodes, trustedNodes);
        maxLatency = Math.max(maxLatency, maxConnLatency);
        network.setMaxLatency(maxLatency);
    }

    /**
     * Starts this Simulation.
     */  
    public void start() {
        while(success+failure < p.getRuns()){
            p.next();
            if(success+failure == 0 || resetPeers)
                createPeers();
            network.run();
        }
        System.out.println("Successful Double Spends: "+success);
        System.out.println("Trusted Orphan Rate: "+((double)tOrphans)/tBlocks);
        System.out.println("Attacker Orphan Rate: "+((double)aOrphans)/aBlocks);
    }
    
    /**
     * Called after (un-)successful Double Spend attempt. Stops the network so
     * a new run of the Simulation can be started.
     * @param successful Wether the Double Spend attempt was successful
     * @param attackerChain The final length of the attacker fork of the Blockchain
     * @param trustedChain The final length of the Blockchain created by the Network of trusted Nodes
     * @param attackerOrphans The number of orphan blocks mined by the attacking Network
     * @param trustedOrphans The number of orphan blocks mined by the trusted Network
     */
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
