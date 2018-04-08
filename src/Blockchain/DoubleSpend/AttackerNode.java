package Blockchain.DoubleSpend;

import Blockchain.Blockchain;
import Blockchain.Network;
import Blockchain.Node;
import Blockchain.Parameters;

/**
 * Implementation of a rogue Node trying to create Double Spends in the Network.
 */
public class AttackerNode extends Node{
    private DSManager dsm;
    
    public AttackerNode(DSManager dsm, Network network, Parameters p, String name) {
        super(network, p, new DSBlockchain(true), name);
        this.dsm = dsm;
    }
    
    @Override
    protected void onBlockMined(){
        dsm.registerAttackerChain(blockchain.getLength());
    }
    
    @Override
    protected boolean ignoreBlockchain(Blockchain newChain, Node sender) {
        return !((DSBlockchain) newChain).isInfested();
    }
}
