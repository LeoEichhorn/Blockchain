package Blockchain;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public abstract class Node {    
    protected Simulation sim;
    private Parameters p;
    private double difficulty;
    private List<Peer> peers;
    private Thread miningThread;
    private int blockchain;
    private String name;
    
    public Node(Simulation sim, CyclicBarrier gate, Parameters p,  
            ScheduledExecutorService executor, String name){
        this.sim = sim;
        this.p = p;
        this.difficulty = p.getDifficulty();
        this.peers = new LinkedList<>();
        this.name = name;
        reset(gate, executor);
    }
    
    public final void reset(CyclicBarrier gate, ScheduledExecutorService executor) {
        blockchain = 0;
        miningThread = new Thread(() -> {
            synchronize(gate);
            while (!sim.stopped()) {
                for (int i = 0; i < 10; i++) {
                    if (ThreadLocalRandom.current().nextDouble() <= difficulty) {
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
        String name = Thread.currentThread().getName();
        if(p.getLogLevel().intValue() <= Level.FINER.intValue())
            System.out.println(name+" found Block! Sending chain of length "+toSend+" to Peers...");
        peers.stream().forEach(peer -> {
            executor.schedule(() -> {
                peer.getNode().recieveBlockchain(toSend, name);
            }, peer.getLatency(), TimeUnit.MILLISECONDS);
        });
        registerBlockchain(blockchain);
    }
    
    public synchronized void recieveBlockchain(int newChain, String sender) {
        if(newChain > blockchain){
            if(p.getLogLevel().intValue() <= Level.FINEST.intValue())
                System.out.println(name+": Accepting new Blockchain of length "+newChain+" from "+sender);
            blockchain = newChain;
        }else if(p.getLogLevel().intValue() <= Level.FINEST.intValue()){
            System.out.println(name+": Declining new Blockchain of length "+newChain+" from "+sender);
        }
    }
    
    public void addPeer(Peer p) {
        peers.add(p);
    }

    protected abstract void registerBlockchain(int blockchain);
}
