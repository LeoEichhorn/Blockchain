package Blockchain;

import Blockchain.Peers.Peer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Basic implementation of a mining Node.
 */
public class Node { 
    //Network this Node is in
    private final Network network;
    
    //List of peers this Node is connected to
    private List<Peer> peers;
     
    //This node's copy of the Blockchain
    protected Blockchain blockchain;
    
    protected final Parameters p;
    private Thread miningThread;
    protected final String name;
    
    public Node(Network network, Parameters p, Blockchain blockchain, String name){
        this.network = network;
        this.p = p;
        this.peers = new LinkedList<>();
        this.blockchain = blockchain;
        this.name = name;
    }
    
    /**
     * Resets this node's Blockchain and creates a new mining thread so 
     * the mining procedure can be (re-)started.
     * @param gate The gate used to synchronize nodes
     * @param executor The executor used to schedule transmissions of Blockchains to Peers
     */
    public final void reset(CyclicBarrier gate, ScheduledExecutorService executor) {
        blockchain.reset(this);
        miningThread = new Thread(() -> {
            synchronize(gate);
            while (!network.stopped()) {
                for (int i = 0; i < 10; i++) {
                    if (ThreadLocalRandom.current().nextDouble() <= p.getDifficulty()) {
                        blockFound(executor);   
                    }
                }
                synchronize(gate);
            }
            if (!gate.isBroken()) {
                gate.reset();
            }
        }, name);
    }
    
    private void synchronize(CyclicBarrier gate){
        try {
            gate.await();
        } catch (InterruptedException | BrokenBarrierException ex) {}
    }

    /**
     * Starts this node's mining thread.
     */
    public void start() {
        miningThread.start();
    }
    
    /**
     * Joins this node's mining thread.
     */
    public void join() {
        try{
            miningThread.join();
        }catch(InterruptedException e){}    
    }
    
    /**
     * Called once a new block is found. Schedules transmission of the newly
     * found block to this node's peers.
     */
    private synchronized void blockFound(ScheduledExecutorService executor) {
        blockchain.addBlock();
        
        if(p.getLogLevel().intValue() <= Level.FINER.intValue())
            System.out.println(name+" found Block! Sending chain of length "+blockchain.getLength()+" to Peers...");
        
        peers.stream().forEach(peer -> {
            Blockchain toSend = blockchain.copy();
            executor.schedule(() -> {
                peer.getNode().recieveBlockchain(toSend, this);
            }, peer.getLatency(), TimeUnit.MILLISECONDS);
        });
        
        onBlockMined();
    }
    
    /**
     * Called when this node recieves a new Block by one of its peers.
     * Only Blockchains longer than this node's copy are accepted.
     * @param newChain The recieved Blockchain
     * @param sender The sending Node
     */
    public synchronized void recieveBlockchain(Blockchain newChain, Node sender) {
        if(ignoreBlockchain(newChain, sender))
            return;
        Blockchain oldChain = blockchain;
        if(newChain.compareTo(blockchain) > 0){
            if(p.getLogLevel().intValue() <= Level.FINEST.intValue())
                System.out.println(name+": Accepting new Blockchain "+newChain+" from "+sender.getName());
            blockchain = newChain;
        }else if(p.getLogLevel().intValue() <= Level.FINEST.intValue()){
            System.out.println(name+": Declining new Blockchain "+newChain+" from "+sender.getName());
        }
        onChoice(oldChain, blockchain);
    }
    
    public void addPeer(Peer p) {
        peers.add(p);
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Decides if a recieved Blockchain should be ignored.
     * @param newChain The recieved Blockchain
     * @param sender The sending Node
     * @return If newChain should be ignored
     */
    protected boolean ignoreBlockchain(Blockchain newChain, Node sender) {
        return false;
    }
    
    /**
     * Called after the decision wether mining should continue on the new Blockchain or not.
     * @param oldChain The Blockchain that was originally mined on
     * @param newChain The newly accepted Blockchain (might be equal to oldChain)
     */
    protected void onChoice(Blockchain oldChain, Blockchain newChain) {}

    /**
     * Called after a new Block has been mined and transmitted to this Node's Peers
     */
    protected void onBlockMined() {}
}
