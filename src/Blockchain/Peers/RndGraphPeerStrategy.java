package Blockchain.Peers;

import Blockchain.Node;
import Blockchain.Util.GraphUtil;
import java.util.ArrayList;
import java.util.LinkedList;

public class RndGraphPeerStrategy extends GraphPeerStrategy{
    private int n;
    private int edges;
    private long meanLatency;
    private double stdDeviation;
    
    /**
     * Creates a random network of peers. Latencies are sampled from a 
     * normal distribution with constant mean and standard deviation.
     * @param n The number of Nodes
     * @param edges The number of Edges
     * @param meanLatency The mean of the normal distribution
     * @param stdDeviation The standard deviation of the normal distribution
     */
    public RndGraphPeerStrategy(int n, int edges, long meanLatency, double stdDeviation) {
        this.n = n;
        this.edges = edges;
        this.meanLatency = meanLatency;
        this.stdDeviation = stdDeviation;
    }
    
    @Override
    public long connectPeers(ArrayList<Node> nodes) {
        ArrayList<LinkedList<GraphUtil.EdgeTo>> adj = GraphUtil.rndGraph(n, edges, meanLatency, stdDeviation);
        return connectPeersInGraph(adj, nodes);
    }

}
