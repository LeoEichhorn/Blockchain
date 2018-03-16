package Blockchain;

public class TrustedNode extends Node{

    public TrustedNode(Blocks blocks) {
        super(blocks);
    }
    
    @Override
    public void run(){
        while(!blocks.stopped()){
            mine();
            blocks.addToTrustedChain();
        }
    }
}
