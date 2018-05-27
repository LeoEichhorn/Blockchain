package DoubleSpend;

import Blockchain.Node;
import Blockchain.Util.Parameter;
import Blockchain.Peers.Peer;
import Blockchain.Util.Util;
import java.util.ArrayList;
import java.util.Random;

public class ConstantConnectionStrategy extends ConnectionStrategy{
    
    private Parameter<Integer> mean;
    
    /**
     * Each attacker node is connected with each trusted node by a latency 
     * sampled from a normal distribution with constant mean and standard deviation
     * @param mean The integer parameter containing the mean latency between any two nodes
     */
    public ConstantConnectionStrategy(Parameter<Integer> mean) {
        this.mean = mean;
    }

    @Override
    public long connectPeers(ArrayList<Node> attackers, ArrayList<Node> trusted) {
        Random rnd = new Random();
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

}
