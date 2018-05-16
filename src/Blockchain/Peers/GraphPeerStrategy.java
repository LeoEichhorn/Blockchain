package Blockchain.Peers;

import Blockchain.Node;
import Blockchain.Util.GraphUtil;
import Blockchain.Util.GraphUtil.EdgeTo;
import java.util.ArrayList;
import java.util.LinkedList;

public abstract class GraphPeerStrategy extends PeerStrategy{
    
    @Override
    public abstract long connectPeers(ArrayList<Node> nodes);
    
    protected long connectPeersInGraph(ArrayList<LinkedList<EdgeTo>> adj, ArrayList<Node> nodes) {
        long[][] dist = GraphUtil.apsp(adj);
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
