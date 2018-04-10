package OrphanRate;

import Blockchain.Blockchain;
import Blockchain.Network;
import Blockchain.Node;
import Blockchain.Parameters;

public class ORNode extends Node{

    private ORManager orm;
    
    public ORNode(ORManager orm, Network network, Parameters p, String name) {
        super(network, p, new Blockchain(), name);
        this.orm = orm;
    }
    
    @Override
    protected void onBlockMined(){
        orm.registerChain(blockchain.getLength());
    }
}
