package Blockchain;

import Blockchain.Peers.Peer;
import Blockchain.Util.CyclicBarrierUtil;
import Blockchain.Util.Logger;
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
public abstract class Node { 
    //Network this Node is in
    private final Network network;
    
    //List of peers this Node is connected to
    private List<Peer> peers;
     
    //This node's copy of the Blockchain
    protected Blockchain blockchain;
    
    private Thread miningThread;
    protected final String name;
    
    /**
     * Creates a new Node
     * @param network The network this Node is in.
     * @param blockchain Initial copy of the Blockchain
     * @param name The name of this Node
     */
    public Node(Network network, Blockchain blockchain, String name){
        this.network = network;
        this.peers = new LinkedList<>();
        this.blockchain = blockchain;
        this.name = name;
    }
    
    /**
     * Resets this node's Blockchain and creates a new mining thread so 
     * the mining procedure can be (re-)started.
     * @param gate The gate used to synchronize Nodes
     * @param executor The ScheduledExecutorService used to schedule transmissions of Blockchains to Peers
     */
    public final void reset(CyclicBarrier gate, ScheduledExecutorService executor) {
        blockchain.reset(this);
        miningThread = new Thread(() -> {
            synchronize(gate);
            while (!network.stopped()) {
                for (int i = 0; i < 10; i++) {
                    if (ThreadLocalRandom.current().nextDouble() <= blockchain.getDifficulty()) {
                        blockFound(executor);   
                    }
                }
                synchronize(gate);
            }
            CyclicBarrierUtil.breakCyclicBarrier(gate);
        }, name);
    }
    
    private void synchronize(CyclicBarrier gate){
        try {
            gate.await();
        } catch (InterruptedException | BrokenBarrierException ex) {}
    }

    /**
     * Node should be reset first.
     * Starts this Node's mining thread.
     */
    public void startMining() {
        if(miningThread != null)
            miningThread.start();
    }
    
    /**
     * Joins this node's mining thread.
     */
    public final void join() {
        if(miningThread != null){
            try{
                miningThread.join();
            }catch(InterruptedException e){}    
        }
    }
    
    /**
     * Called once a new block is found. Schedules transmission of the newly
     * found block to this node's peers.
     */
    private synchronized void blockFound(ScheduledExecutorService executor) {
        blockchain.addBlock();
        
        Logger.log(Level.FINER, String.format("%s found Block! Sending chain of length %d to Peers...",
                name, blockchain.getLength()));
        
        peers.stream().forEach(peer -> {
            final Blockchain toSend = blockchain.copy();
            executor.schedule(() -> {
                peer.getNode().receiveBlockchain(toSend, this);
            }, peer.getLatency(), TimeUnit.MILLISECONDS);
        });
        
        onBlockMined();
    }
    
    /**
     * Called when this Node recieves a new Block by one of its Peers.
     * Only Blockchains longer than this Node's copy are accepted.
     * @param newChain The recieved Blockchain
     * @param sender The sending Node
     */
    public synchronized void receiveBlockchain(Blockchain newChain, Node sender) {
        if(ignoreBlockchain(newChain, sender))
            return;
        
        Blockchain oldChain = blockchain;
        if(newChain.compareTo(blockchain) > 0){
            
            Logger.log(Level.FINEST, String.format("%s: Accepting new Blockchain %s from %s",
                name, newChain, sender.getName()));
            
            blockchain = newChain;
            
        }else{
            Logger.log(Level.FINEST, String.format("%s: Declining new Blockchain %s from %s",
                name, newChain, sender.getName()));
        }
        
        onChoice(oldChain, blockchain);
    }
    
    public final void addPeer(Peer p) {
        peers.add(p);
    }
    
    /**
     * Deletes this Node's peer entries
     */
    public final void clearPeers() {
        peers.clear();
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Decides wether a recieved Blockchain should be ignored.
     * @param newChain The recieved Blockchain
     * @param sender The sending Node
     * @return If newChain should be ignored
     */
    protected abstract boolean ignoreBlockchain(Blockchain newChain, Node sender);
    
    /**
     * Called after the decision was made, wether mining should continue on 
     * the new Blockchain or the existing one.
     * @param oldChain The Blockchain that was originally mined on
     * @param newChain The newly accepted Blockchain (might be equal to oldChain)
     */
    protected abstract void onChoice(Blockchain oldChain, Blockchain newChain);

    /**
     * Called after a new Block has been mined and transmitted to this Node's Peers
     */
    protected abstract void onBlockMined();
}
