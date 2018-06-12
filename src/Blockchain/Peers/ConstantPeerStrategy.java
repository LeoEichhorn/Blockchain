package Blockchain.Peers;

import Blockchain.Util.Randomizable;
import Blockchain.Node;
import Blockchain.Util.Util;
import java.util.ArrayList;
import java.util.Random;

public class ConstantPeerStrategy extends PeerStrategy{
    
    private Randomizable<Integer> mean;
    
    /**
     * All nodes in the network are connected by a latency sampled from
     * a normal distribution with constant mean and standard deviation
     * @param mean The integer parameter containing the mean latency between any two nodes
     */
    public ConstantPeerStrategy(Randomizable<Integer> mean) {
        if(mean.getValue()<0||mean.getBounds()[0]<0)
            throw new IllegalArgumentException("Negative latency mean");
        this.mean = mean;
    }

    @Override
    public long connectPeers(ArrayList<Node> nodes) {
        for(Node n : nodes)
            n.resetPeers();
        Random rnd = new Random();
        mean.next();
        long max = 0;
        for(int i = 0; i < nodes.size(); i++) {
            for(int j = i+1; j < nodes.size(); j++) {
                long latency = (long) Util.nextGaussian(rnd, mean.getValue());
                max = Math.max(max, latency);
                nodes.get(i).addPeer(new Peer(nodes.get(j), latency));
                nodes.get(j).addPeer(new Peer(nodes.get(i), latency));
            }
        }
        return max;
    }
    
    @Override
    public String toString() {
        String r = "CONSTANT, Latency: ";
        if(mean.isRandomized()){
            Integer[] b = mean.getBounds();
            r += "random["+b[0]+";"+b[1]+"]";
        }else{
            r += mean.getValue();
        }
        return r;
    }
}
