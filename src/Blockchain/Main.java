package Blockchain;

import java.util.concurrent.CyclicBarrier;

public class Main{
    public static void main(String[] args) throws InterruptedException {
        int num = Params.NUM_ATTACKER+Params.NUM_TRUSTED;
        Blocks blocks = new Blocks();
        CyclicBarrier gate = new CyclicBarrier(num);
        Node[] nodes = new Node[num];
        
        for (int i = 0; i < Params.NUM_TRUSTED; i++) {
            nodes[i] = new TrustedNode(blocks, gate);
        }
        for (int i = Params.NUM_TRUSTED; i < num; i++) {
            nodes[i] = new AttackerNode(blocks, gate);
        }
        
        for (Node n : nodes) {
            n.start();
        }
        for (Node n : nodes) {
            n.join();
        }
        
        System.out.println("Successful Double Spends: "+blocks.getSuccess());
    }
}
