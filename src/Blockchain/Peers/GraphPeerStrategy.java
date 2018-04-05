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
    
    public GraphPeerStrategy(int n, int edges, long meanLatency, double stdDeviation) {
        this(GraphUtil.rndGraph(n, edges, meanLatency, stdDeviation));
    }
    
    public GraphPeerStrategy(boolean[][] m, long mean, double stdDev, boolean symmetric){
        this(GraphUtil.fromBoolMatrix(m, mean, stdDev, symmetric));
    }
    
    public GraphPeerStrategy(long[][] m){
        this(GraphUtil.fromAdjMatrix(m));
    }
    
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
