package Blockchain.Peers;

import Blockchain.Node;
import java.util.ArrayList;

public abstract class PeerStrategy {   

    /**
     * Connects all nodes according to the concrete implementation.
     * @param nodes The nodes to be connected.
     * @return The maximum latency between any two peers in the created network
     */
    public abstract long connectPeers(ArrayList<Node> nodes);
}
