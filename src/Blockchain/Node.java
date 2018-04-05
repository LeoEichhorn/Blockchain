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

public abstract class Node {    
    private final Network network;
    private final Parameters p;
    private List<Peer> peers;
    private Thread miningThread;
    private int blockchain;
    private final String name;
    
    public Node(Network network, Parameters p, String name){
        this.network = network;
        this.p = p;
        this.peers = new LinkedList<>();
        this.name = name;
    }
    
    public final void reset(CyclicBarrier gate, ScheduledExecutorService executor) {
        blockchain = 0;
        miningThread = new Thread(() -> {
            synchronize(gate);
            while (!network.stopped()) {
                for (int i = 0; i < 10; i++) {
                    if (ThreadLocalRandom.current().nextDouble() <= p.getDifficulty()) {
                        onMinedBlock(executor);   
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

    public void start() {
        miningThread.start();
    }
    
    public void join() {
        try{
            miningThread.join();
        }catch(InterruptedException e){}    
    }
    
    private synchronized void onMinedBlock(ScheduledExecutorService executor) {
        int toSend = ++blockchain;
        if(p.getLogLevel().intValue() <= Level.FINER.intValue())
            System.out.println(name+" found Block! Sending chain of length "+toSend+" to Peers...");
        peers.stream().forEach(peer -> {
            executor.schedule(() -> {
                peer.getNode().recieveBlockchain(toSend, this);
            }, peer.getLatency(), TimeUnit.MILLISECONDS);
        });
        registerBlockchain(blockchain);
    }
    
    public synchronized void recieveBlockchain(int newChain, Node sender) {
        if(newChain > blockchain){
            if(p.getLogLevel().intValue() <= Level.FINEST.intValue())
                System.out.println(name+": Accepting new Blockchain of length "+newChain+" from "+sender.getName());
            blockchain = newChain;
        }else if(p.getLogLevel().intValue() <= Level.FINEST.intValue()){
            System.out.println(name+": Declining new Blockchain of length "+newChain+" from "+sender.getName());
        }
    }
    
    public void addPeer(Peer p) {
        peers.add(p);
    }
    
    public String getName() {
        return name;
    }

    protected abstract void registerBlockchain(int blockchain);
}
