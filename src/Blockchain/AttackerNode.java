package Blockchain;

public class AttackerNode extends Node{

    public AttackerNode(Blocks blocks) {
        super(blocks);
    }
    
    @Override
    public void run(){
        while(!blocks.stopped()){
            mine();
            blocks.addToAttackerChain();
        }
    }
}
