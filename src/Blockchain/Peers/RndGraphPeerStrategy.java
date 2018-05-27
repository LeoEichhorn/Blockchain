package Blockchain.Peers;

import Blockchain.Util.Parameter;
import Blockchain.Node;
import Blockchain.Util.GraphUtil;
import java.util.ArrayList;
import java.util.LinkedList;

public class RndGraphPeerStrategy extends GraphPeerStrategy{
    private int nodeNum;
    private Parameter<Double> density;
    private Parameter<Integer> mean;
    
    /**
     * Creates a random network of peers. Latencies are sampled from a 
     * normal distribution with constant mean and standard deviation.
     * @param nodeNum The number of Nodes
     * @param density The density of edges in the Network
     * @param mean The integer parameter containing the mean latency between any two nodes
     */
    public RndGraphPeerStrategy(int nodeNum, Parameter<Double> density, Parameter<Integer> mean) {
        this.nodeNum = nodeNum;
        this.density = density;
        this.mean = mean;
    }
    
    @Override
    public long connectPeers(ArrayList<Node> nodes) {
        int edges = (int) (density.getValue()*nodeNum*(nodeNum-1))/2;
        ArrayList<LinkedList<GraphUtil.EdgeTo>> adj = GraphUtil.rndGraph(nodeNum, edges, mean.getValue());
        return connectPeersInGraph(adj, nodes);
    }

}
