package StaleBlocks;

import Blockchain.Blockchain;
import Blockchain.Network;
import Blockchain.Node;
import DoubleSpend.Parameters;

public class SBNode extends Node{

    private SBManager orm;
    
    public SBNode(SBManager orm, Network network, Parameters p, String name) {
        super(network, new SBBlockchain(p.getDifficulty()), name);
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
