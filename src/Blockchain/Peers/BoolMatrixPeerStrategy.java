package Blockchain.Peers;

import Blockchain.Util.Parameter;
import Blockchain.Node;
import Blockchain.Util.GraphUtil;
import java.util.ArrayList;
import java.util.LinkedList;

public class BoolMatrixPeerStrategy extends GraphPeerStrategy{
    private int[][] m;
    private boolean symmetric;
    private Parameter<Integer> mean;
    
    /**
     * Creates a network of peers as defined by an adjacency matrix whereas a value > 0
     * implies a connection between peers. Latencies are sampled from a 
     * normal distribution with constant mean and standard deviation.
     * @param m The adjacency Matrix
     * @param symmetric Defines if latencies between two nodes should be symmetric
     * @param mean The integer parameter containing the mean latency between any two nodes
     */
    public BoolMatrixPeerStrategy(int[][] m, boolean symmetric, Parameter<Integer> mean) {
        this.m = m;
        this.symmetric = symmetric;
        this.mean = mean;
    }
    
    
    @Override
    public long connectPeers(ArrayList<Node> nodes) {
        ArrayList<LinkedList<GraphUtil.EdgeTo>> adj 
                = GraphUtil.fromBoolMatrix(m, mean.getValue(), symmetric); 
        return connectPeersInGraph(adj, nodes);
    }

}
