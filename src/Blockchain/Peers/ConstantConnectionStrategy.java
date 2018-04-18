package Blockchain.Peers;

import Blockchain.Node;
import Blockchain.Parameters.IntParameter;
import Blockchain.Util.Util;
import java.util.ArrayList;
import java.util.Random;

public class ConstantConnectionStrategy extends ConnectionStrategy{
    
    private IntParameter mean;
    private double deviationFactor;
    
    /**
     * Each attacker node is connected with each trusted node by a latency 
     * sampled from a normal distribution with constant mean and standard deviation
     * @param mean
     * @param deviationFactor
     */
    public ConstantConnectionStrategy(IntParameter mean, double deviationFactor) {
        this.mean = mean;
        this.deviationFactor = deviationFactor;
    }

    @Override
    public long connectPeers(ArrayList<Node> attackers, ArrayList<Node> trusted) {
        Random rnd = new Random();
        long max = 0;
        for(Node a : attackers) {
            for(Node t : trusted) {
                long latency = (long) Util.nextGaussian(rnd, mean.getValue(), deviationFactor*mean.getValue());
                max = Math.max(max, latency);
                a.addPeer(new Peer(t, latency));
                t.addPeer(new Peer(a, latency));
            }
        }
        return max;
    }

}
