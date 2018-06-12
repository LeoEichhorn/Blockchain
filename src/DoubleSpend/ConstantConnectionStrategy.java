package DoubleSpend;

import Blockchain.Node;
import Blockchain.Util.Randomizable;
import Blockchain.Peers.Peer;
import Blockchain.Util.Util;
import java.util.ArrayList;
import java.util.Random;

public class ConstantConnectionStrategy extends ConnectionStrategy{
    
    private Randomizable<Integer> mean;
    
    /**
     * Each attacker node is connected with each trusted node by a latency 
     * sampled from a normal distribution with constant mean and standard deviation
     * @param mean The integer parameter containing the mean latency between any two nodes
     */
    public ConstantConnectionStrategy(Randomizable<Integer> mean) {
        this.mean = mean;
    }

    @Override
    public long connectPeers(ArrayList<Node> attackers, ArrayList<Node> trusted) {
        Random rnd = new Random();
        mean.next();
        long max = 0;
        for(Node a : attackers) {
            for(Node t : trusted) {
                long latency = (long) Util.nextGaussian(rnd, mean.getValue());
                max = Math.max(max, latency);
                a.addPeer(new Peer(t, latency));
                t.addPeer(new Peer(a, latency));
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
