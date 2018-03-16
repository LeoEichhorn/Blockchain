package Blockchain;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Node extends Thread {
    
    protected Blocks blocks;
    
    public Node(Blocks blocks){
        this.blocks = blocks;
    }
    
    //Mining a block consists of finding a random double smaller than the difficulty
    protected void mine(){
        ThreadLocalRandom.current().doubles()
                .anyMatch(r -> r <= Params.DIFFICULTY);
    }
    
    @SuppressWarnings("empty-statement")
    protected void mine2(){
        while(ThreadLocalRandom.current().nextDouble() > Params.DIFFICULTY);
    }
}
