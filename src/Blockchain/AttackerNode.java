package Blockchain;

import java.util.concurrent.CyclicBarrier;

public class AttackerNode extends Node{

    public AttackerNode(Blocks blocks, CyclicBarrier gate) {
        super(blocks, gate);
    }
    
    @Override
    public void handleBlock(){
        blocks.addToAttackerChain();
    }
}
