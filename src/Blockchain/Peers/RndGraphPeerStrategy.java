package Blockchain.Peers;

import Blockchain.Util.Randomizable;
import Blockchain.Node;
import Blockchain.Util.GraphUtil;
import java.util.ArrayList;
import java.util.LinkedList;

public class RndGraphPeerStrategy extends GraphPeerStrategy{
    private Randomizable<Double> density;
    private Randomizable<Integer> mean;
    
    /**
     * Creates a random network of peers. Latencies are sampled from a 
     * normal distribution with constant mean and standard deviation.
     * @param density The density of edges in the Network
     * @param mean The integer parameter containing the mean latency between any two nodes
     */
    public RndGraphPeerStrategy(Randomizable<Double> density, Randomizable<Integer> mean) {
        this.density = density;
        this.mean = mean;
    }
    
    @Override
    public long connectPeers(ArrayList<Node> nodes) {
        for(Node n : nodes)
            n.clearPeers();
        int nodeNum = nodes.size();
        int edges = (int) (density.next()*nodeNum*(nodeNum-1))/2;
        ArrayList<LinkedList<GraphUtil.EdgeTo>> adj = GraphUtil.rndGraph(nodeNum, edges, mean.next());
        return connectPeersInGraph(adj, nodes);
    }
    
    @Override
    public String toString() {
        String r = "RANDOM, Latency: ";
        if(mean.isRandomized()){
            Integer[] b = mean.getBounds();
            r += "random["+b[0]+";"+b[1]+"]";
        }else{
            r += mean.getValue();
        }
        r += ", Density: ";
        if(density.isRandomized()){
            Double[] b = density.getBounds();
            r += "random["+b[0]+";"+b[1]+"]";
        }else{
            r += density.getValue();
        }
        return r;
    }
}
