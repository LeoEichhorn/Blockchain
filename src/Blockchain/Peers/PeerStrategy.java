package Blockchain.Peers;

import Blockchain.Node;
import java.util.ArrayList;

public abstract class PeerStrategy {   
    public abstract long connectPeers(ArrayList<Node> nodes);
}
