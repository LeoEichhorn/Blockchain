package Blockchain.Peers;

import Blockchain.Node;
import Blockchain.Parameters.IntParameter;
import Blockchain.Util.Util;
import java.util.ArrayList;
import java.util.Random;

public class ConstantPeerStrategy extends PeerStrategy{
    
    private IntParameter mean;
    private double deviationFactor;
    
    /**
     * All nodes in the network are connected by a latency sampled from
     * a normal distribution with constant mean and standard deviation
     * @param mean
     * @param deviationFactor
     */
    public ConstantPeerStrategy(IntParameter mean, double deviationFactor) {
        this.mean = mean;
        this.deviationFactor = deviationFactor;
    }

    @Override
    public long connectPeers(ArrayList<Node> nodes) {
        Random rnd = new Random();
        long max = 0;
        for(int i = 0; i < nodes.size(); i++) {
            for(int j = i+1; j < nodes.size(); j++) {
                long latency = (long) Util.nextGaussian(rnd, mean.getValue(), deviationFactor*mean.getValue());
                max = Math.max(max, latency);
                nodes.get(i).addPeer(new Peer(nodes.get(j), latency));
                nodes.get(j).addPeer(new Peer(nodes.get(i), latency));
            }
        }
        return max;
    }


}
