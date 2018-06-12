package Blockchain.Peers;

import Blockchain.Node;
import Blockchain.Util.GraphUtil;
import java.util.ArrayList;
import java.util.LinkedList;

public class AdjMatrixPeerStrategy extends GraphPeerStrategy{
    private long[][] m;
    
    /**
     * Creates a network of peers as defined by an adjacency matrix 
     * whose entries are the latencies.
     * @param m The adjacency Matirx
     */
    public AdjMatrixPeerStrategy(long[][] m){
        this.m = m;
    }
    
    
    @Override
    public long connectPeers(ArrayList<Node> nodes) {
        for(Node n : nodes)
            n.resetPeers();
        ArrayList<LinkedList<GraphUtil.EdgeTo>> adj 
                = GraphUtil.fromAdjMatrix(m);
        return connectPeersInGraph(adj, nodes);
    }

    @Override
    public String toString() {
        return "ADJACENCY";
    }
}
