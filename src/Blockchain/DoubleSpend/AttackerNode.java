package Blockchain.DoubleSpend;

import Blockchain.Network;
import Blockchain.Node;
import Blockchain.Parameters;

public class AttackerNode extends Node{
    private DoubleSpendManager dsm;
    
    public AttackerNode(DoubleSpendManager dsm, Network network, Parameters p, String name) {
        super(network, p, name);
        this.dsm = dsm;
    }
    
    @Override
    protected void registerBlockchain(int blockchain){
        dsm.registerAttackerChain(blockchain);
    }
}
