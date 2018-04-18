package Blockchain.Peers;

import Blockchain.Node;
import Blockchain.Parameters.DoubleParameter;
import Blockchain.Parameters.IntParameter;
import Blockchain.Util.GraphUtil;
import java.util.ArrayList;
import java.util.LinkedList;

public class RndGraphPeerStrategy extends GraphPeerStrategy{
    private int n;
    private DoubleParameter density;
    private IntParameter mean;
    private double deviationFactor;
    
    /**
     * Creates a random network of peers. Latencies are sampled from a 
     * normal distribution with constant mean and standard deviation.
     * @param n The number of Nodes
     * @param density
     * @param mean
     * @param deviationFactor
     */
    public RndGraphPeerStrategy(int n, DoubleParameter density, IntParameter mean, double deviationFactor) {
        this.n = n;
        this.density = density;
        this.mean = mean;
        this.deviationFactor = deviationFactor;
    }
    
    @Override
    public long connectPeers(ArrayList<Node> nodes) {
        int edges = (int) (density.getValue()*n*(n-1))/2;
        ArrayList<LinkedList<GraphUtil.EdgeTo>> adj = GraphUtil.rndGraph(n, edges, mean.getValue(), deviationFactor*mean.getValue());
        return connectPeersInGraph(adj, nodes);
    }

}
