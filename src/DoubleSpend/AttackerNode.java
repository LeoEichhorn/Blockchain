package DoubleSpend;

import Blockchain.Blockchain;
import Blockchain.Network;
import Blockchain.Node;

/**
 * Implementation of a rogue Node trying to create Double Spends in the Network.
 */
public class AttackerNode extends Node{
    private DSManager dsm;
    
    public AttackerNode(DSManager dsm, Network network, Parameters p, String name) {
        super(network, new DSBlockchain(p.getDifficulty(), false), p.getLogLevel(), name);
        this.dsm = dsm;
    }
    
    @Override
    protected void onBlockMined(){
        dsm.registerAttackerChain(blockchain.getLength());
    }
    
    @Override
    protected boolean ignoreBlockchain(Blockchain newChain, Node sender) {
        //Only infested Blockchains (containing the altered transaction) are accepted
        return !((DSBlockchain) newChain).isInfested();
    }
}
