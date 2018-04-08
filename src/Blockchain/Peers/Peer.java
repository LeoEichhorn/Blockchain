package Blockchain.Peers;

import Blockchain.Node;

public class Peer {
    private final Node node;
    private final long latency;

    public Peer(Node node, long latency) {
        this.node = node;
        this.latency = latency;
    }

    public Node getNode() {
        return node;
    }

    /**
     * @return the latency to reach this Node
     */
    public long getLatency() {
        return latency;
    }  
}
