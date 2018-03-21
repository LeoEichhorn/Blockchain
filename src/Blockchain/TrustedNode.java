package Blockchain;

import java.util.concurrent.CyclicBarrier;

public class TrustedNode extends Node{

    public TrustedNode(Blocks blocks, CyclicBarrier gate) {
        super(blocks, gate);
    }
    
    @Override
    public void handleBlock(){
        blocks.addToTrustedChain();
    }
}
