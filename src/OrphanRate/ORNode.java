package OrphanRate;

import Blockchain.Blockchain;
import Blockchain.Network;
import Blockchain.Node;
import DoubleSpend.Parameters;

public class ORNode extends Node{

    private ORManager orm;
    
    public ORNode(ORManager orm, Network network, Parameters p, String name) {
        super(network, new Blockchain(p.getDifficulty()), name);
        this.orm = orm;
    }
    
    @Override
    protected void onBlockMined(){
        orm.registerChain(blockchain.getLength());
    }

    @Override
    protected boolean ignoreBlockchain(Blockchain newChain, Node sender) {
        return false;
    }

    @Override
    protected void onChoice(Blockchain oldChain, Blockchain newChain) {}
}
