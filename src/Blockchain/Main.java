package Blockchain;

import java.io.IOException;
import java.util.concurrent.CyclicBarrier;

public class Main{
    public static void main(String[] args) throws InterruptedException {  
        try {
            Params.loadParameters("parameters.txt");
        } catch (IOException ex) {
            System.err.println("Error loading parameters.");
            System.exit(1);
        }
        
        System.out.printf("Attempting %d double spends on a Blockchain with\n"
                + "mining difficulty %s and %d confirmations.\n"
                + "Network: %d trusted and %d attacking nodes.\n", 
                Params.RUNS, ""+Params.DIFFICULTY, Params.CONFIRMATIONS, 
                Params.NUM_TRUSTED, Params.NUM_ATTACKER);
                
        
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
