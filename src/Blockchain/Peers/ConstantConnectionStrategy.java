package Blockchain.Peers;

import Blockchain.Node;
import Blockchain.Util.Util;
import java.util.ArrayList;
import java.util.Random;

public class ConstantConnectionStrategy extends ConnectionStrategy{
    
    private long mean;
    private double stdDev;
    
    /**
     * Each attacker node is connected with each trusted node by a latency 
     * sampled from a normal distribution with constant mean and standard deviation
     * @param mean The latency mean
     * @param stdDev The latency standard deviation
     */
    public ConstantConnectionStrategy(long mean, double stdDev) {
        this.mean = mean;
        this.stdDev = stdDev;
    }

    @Override
    public long connectPeers(ArrayList<Node> attackers, ArrayList<Node> trusted) {
        Random rnd = new Random();
        long max = 0;
        for(Node a : attackers) {
            for(Node t : trusted) {
                long latency = (long) Util.nextGaussian(rnd, mean, stdDev);
                max = Math.max(max, latency);
                a.addPeer(new Peer(t, latency));
                t.addPeer(new Peer(a, latency));
            }
        }
        return max;
    }

}
