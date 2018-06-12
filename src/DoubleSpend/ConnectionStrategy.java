package DoubleSpend;

import Blockchain.Node;
import java.util.ArrayList;

/**
 * Strategy of connecting Attacker Network with the Trusted Network.
 */
public abstract class ConnectionStrategy {

    /**
     * Connects the Attacker Network with the Trusted Network according to the concrete implementation.
     * @param attackers List of Nodes in the Attacker Network
     * @param trusted List of Nodes in the Trusted Network
     * @return The maximum latency between all created connections
     */
    public abstract long connectPeers(ArrayList<Node> attackers, ArrayList<Node> trusted);
    @Override
    public abstract String toString();
}
