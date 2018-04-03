package Blockchain;

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

    public long getLatency() {
        return latency;
    }  
}
