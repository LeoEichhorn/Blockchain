package Blockchain;

public class Main{
    public static void main(String[] args) throws InterruptedException {
        int num = Params.NUM_ATTACKER+Params.NUM_TRUSTED;
        Blocks blocks = new Blocks();
        Node[] nodes = new Node[num];
        
        for (int i = 0; i < Params.NUM_TRUSTED; i++) {
            nodes[i] = new TrustedNode(blocks);
        }
        for (int i = Params.NUM_TRUSTED; i < num; i++) {
            nodes[i] = new AttackerNode(blocks);
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
