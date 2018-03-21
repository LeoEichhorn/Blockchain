package Blockchain;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Node extends Thread {
    
    protected Blocks blocks;
    private CyclicBarrier gate;
    
    public Node(Blocks blocks, CyclicBarrier gate){
        this.blocks = blocks;
        this.gate = gate;
    }
    
    //Mining a block consists of finding a random double smaller than the difficulty
    private void mine(){
        ThreadLocalRandom.current().doubles()
                .anyMatch(r -> r <= Params.DIFFICULTY);
    }
    
    @SuppressWarnings("empty-statement")
    private void mine2(){
        while(ThreadLocalRandom.current().nextDouble() > Params.DIFFICULTY);
    }
    
    protected abstract void handleBlock();
    
    @Override
    public void run(){
        try {
            gate.await();
        } catch (InterruptedException | BrokenBarrierException ex) {}
        
        while(!blocks.stopped()){
            mine();
            handleBlock();
        }
    }
}
