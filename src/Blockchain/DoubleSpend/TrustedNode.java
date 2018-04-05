package Blockchain.DoubleSpend;

import Blockchain.Network;
import Blockchain.Node;
import Blockchain.Parameters;

public class TrustedNode extends Node{
    private DoubleSpendManager dsm;
    
    public TrustedNode(DoubleSpendManager dsm, Network network, Parameters p, String name) {
        super(network, p, name);
        this.dsm = dsm;
    }
    
    @Override
    protected void registerBlockchain(int blockchain){
        dsm.registerTrustedChain(blockchain);
    }
}
