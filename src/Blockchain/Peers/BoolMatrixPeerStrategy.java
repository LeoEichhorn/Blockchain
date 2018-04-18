package Blockchain.Peers;

import Blockchain.Node;
import Blockchain.Parameters.IntParameter;
import Blockchain.Util.GraphUtil;
import java.util.ArrayList;
import java.util.LinkedList;

public class BoolMatrixPeerStrategy extends GraphPeerStrategy{
    private int[][] m;
    private boolean symmetric;
    private IntParameter mean;
    private double deviationFactor;
    
    /**
     * Creates a network of peers as defined by an adjacency matrix where "true"
     * implies a connection between peers. Latencies are sampled from a 
     * normal distribution with constant mean and standard deviation.
     * @param m The adjacency Matrix
     * @param symmetric Defines if latencies between two nodes should be symmetric
     * @param mean
     * @param deviationFactor
     */
    public BoolMatrixPeerStrategy(int[][] m, boolean symmetric, IntParameter mean, double deviationFactor) {
        this.m = m;
        this.symmetric = symmetric;
        this.mean = mean;
        this.deviationFactor = deviationFactor;
    }
    
    
    @Override
    public long connectPeers(ArrayList<Node> nodes) {
        ArrayList<LinkedList<GraphUtil.EdgeTo>> adj 
                = GraphUtil.fromBoolMatrix(m, mean.getValue(), deviationFactor*mean.getValue(), symmetric); 
        return connectPeersInGraph(adj, nodes);
    }

}
