package Blockchain.Peers;

import Blockchain.Node;
import Blockchain.Util.Util;
import java.util.ArrayList;
import java.util.Random;

public class ConstantPeerStrategy extends PeerStrategy{
    
    private long mean;
    private double stdDev;
    
    public ConstantPeerStrategy(long mean, double stdDev) {
        this.mean = mean;
        this.stdDev = stdDev;
    }

    @Override
    public long connectPeers(ArrayList<Node> nodes) {
        Random rnd = new Random();
        long max = 0;
        for(int i = 0; i < nodes.size(); i++) {
            for(int j = i+1; j < nodes.size(); j++) {
                long latency = (long) Util.nextGaussian(rnd, mean, stdDev);
                max = Math.max(max, latency);
                nodes.get(i).addPeer(new Peer(nodes.get(j), latency));
                nodes.get(j).addPeer(new Peer(nodes.get(i), latency));
            }
        }
        return max;
    }


}
