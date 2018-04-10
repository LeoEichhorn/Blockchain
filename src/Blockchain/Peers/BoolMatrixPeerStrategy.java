package Blockchain.Peers;

import Blockchain.Node;
import Blockchain.Util.GraphUtil;
import java.util.ArrayList;
import java.util.LinkedList;

public class BoolMatrixPeerStrategy extends GraphPeerStrategy{
    private int[][] m;
    private boolean symmetric;
    private long meanLatency;
    private double stdDeviation;
    
    /**
     * Creates a network of peers as defined by an adjacency matrix where "true"
     * implies a connection between peers. Latencies are sampled from a 
     * normal distribution with constant mean and standard deviation.
     * @param m The adjacency Matrix
     * @param meanLatency The mean of the normal distribution
     * @param stdDeviation The standard deviation of the normal distribution
     * @param symmetric Defines if latencies between two nodes should be symmetric
     */
    public BoolMatrixPeerStrategy(int[][] m, boolean symmetric, long meanLatency, double stdDeviation) {
        this.m = m;
        this.symmetric = symmetric;
        this.meanLatency = meanLatency;
        this.stdDeviation = stdDeviation;
    }
    
    
    @Override
    public long connectPeers(ArrayList<Node> nodes) {
        ArrayList<LinkedList<GraphUtil.EdgeTo>> adj 
                = GraphUtil.fromBoolMatrix(m, meanLatency, stdDeviation, symmetric); 
        return connectPeersInGraph(adj, nodes);
    }

}
