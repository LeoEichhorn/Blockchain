package OrphanRate;

import Blockchain.Blockchain;
import Blockchain.Network;
import Blockchain.Node;
import DoubleSpend.Parameters;

public class ORNode extends Node{

    private ORManager orm;
    
    public ORNode(ORManager orm, Network network, Parameters p, String name) {
        super(network, new Blockchain(p.getDifficulty()), p.getLogLevel(), name);
        this.orm = orm;
    }
    
    @Override
    protected void onBlockMined(){
        orm.registerChain(blockchain.getLength());
    }
}
