package Blockchain;

import java.util.ArrayList;

public abstract class AbstractPeerFactory {   
    public abstract long createPeers(ArrayList<Node> nodes);
}
