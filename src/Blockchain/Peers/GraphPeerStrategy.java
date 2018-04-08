package Blockchain.Peers;

import Blockchain.Node;
import Blockchain.Util.GraphUtil;
import Blockchain.Util.GraphUtil.EdgeTo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class GraphPeerStrategy extends PeerStrategy{
    private long[][] dist;
    private ArrayList<LinkedList<EdgeTo>> adj;
    
    /**
     * Creates a random network of peers. Latencies are sampled from a 
     * normal distribution with constant mean and standard deviation.
     * @param n The number of Nodes
     * @param edges The number of Edges
     * @param meanLatency The mean of the normal distribution
     * @param stdDeviation The standard deviation of the normal distribution
     */
    public GraphPeerStrategy(int n, int edges, long meanLatency, double stdDeviation) {
        this(GraphUtil.rndGraph(n, edges, meanLatency, stdDeviation));
    }
    
    /**
     * Creates a network of peers as defined by an adjacency matrix where "true"
     * implies a connection between peers. Latencies are sampled from a 
     * normal distribution with constant mean and standard deviation.
     * @param m The adjacency Matrix
     * @param mean The mean of the normal distribution
     * @param stdDev The standard deviation of the normal distribution
     * @param symmetric Defines if latencies between two nodes should be symmetric
     */
    public GraphPeerStrategy(boolean[][] m, long mean, double stdDev, boolean symmetric){
        this(GraphUtil.fromBoolMatrix(m, mean, stdDev, symmetric));
    }
    
    /**
     * Creates a network of peers as defined by an adjacency matrix 
     * whose entries are the latencies.
     * @param m The adjacency Matirx
     */
    public GraphPeerStrategy(long[][] m){
        this(GraphUtil.fromAdjMatrix(m));
    }
    
    /**
     * Creates a network of peers as defined by the given adjecency list.
     * @param adj The adjacency List
     */
    public GraphPeerStrategy(ArrayList<LinkedList<EdgeTo>> adj){
        this.dist = GraphUtil.apsp(adj);
        this.adj = adj;
    }

    @Override
    public long connectPeers(ArrayList<Node> nodes) {
//        System.out.println("GRAPH:");
//        for(LinkedList<EdgeTo> l : adj){
//            System.out.println(l);
//        }
//        System.out.println("DIST:");
//        for(long[] l : dist){
//            System.out.println(Arrays.toString(l));
//        }
        long max = 0;
        for(int i = 0; i < nodes.size(); i++){
            for(int j = i+1; j < nodes.size(); j++){
                long latency = dist[i][j];
                max = Math.max(max, latency);
                nodes.get(i).addPeer(new Peer(nodes.get(j), latency));
                nodes.get(j).addPeer(new Peer(nodes.get(i), latency));
            }
        }
        return max;
    }

}
