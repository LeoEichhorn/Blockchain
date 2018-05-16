package Blockchain.Peers;

import Blockchain.Util.Parameter;
import Blockchain.Node;
import Blockchain.Util.GraphUtil;
import java.util.ArrayList;
import java.util.LinkedList;

public class RndGraphPeerStrategy extends GraphPeerStrategy{
    private int n;
    private Parameter<Double> density;
    private Parameter<Integer> mean;
    private double deviationFactor;
    
    /**
     * Creates a random network of peers. Latencies are sampled from a 
     * normal distribution with constant mean and standard deviation.
     * @param n The number of Nodes
     * @param density The density of edges in the Network
     * @param mean The integer parameter containing the mean latency between any two nodes
     * @param deviationFactor The deviation of latency between any two nodes is calculated by deviationFactor*mean
     */
    public RndGraphPeerStrategy(int n, Parameter<Double> density, Parameter<Integer> mean, double deviationFactor) {
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
