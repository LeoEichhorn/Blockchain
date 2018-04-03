package Blockchain;

import java.util.ArrayList;

public class ConstantPeerFactory extends AbstractPeerFactory{

    private long latency;
    
    public ConstantPeerFactory(long latency) {
        this.latency = latency;
    }

    @Override
    public long createPeers(ArrayList<Node> nodes) {
        for(int i = 0; i < nodes.size(); i++) {
            for(int j = i+1; j < nodes.size(); j++) {
                nodes.get(i).addPeer(new Peer(nodes.get(j), latency));
                nodes.get(j).addPeer(new Peer(nodes.get(i), latency));
            }
        }
        return latency;
    }


}
