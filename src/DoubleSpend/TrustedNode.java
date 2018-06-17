package DoubleSpend;

import Blockchain.Blockchain;
import Blockchain.Network;
import Blockchain.Node;
import Blockchain.Util.Logger;
import java.util.logging.Level;

public class TrustedNode extends Node{
    private DSManager dsm;
    private Parameters p;
    
    public TrustedNode(DSManager dsm, Network network, Parameters p, String name) {
        super(network, new DSBlockchain(p.getDifficulty(), false), name);
        this.p = p;
        this.dsm = dsm;
    }
    
    @Override
    protected void onBlockMined(){
        //Check wether the new block was mined on a double-spending chain
        if(((DSBlockchain) blockchain).isDoubleSpending()) {
            dsm.registerAttackerChain(blockchain.getLength());
        } else {
            dsm.registerTrustedChain(blockchain.getLength());
        }
    }
    
    @Override
    protected boolean ignoreBlockchain(Blockchain newChain, Node sender) {
        //Double-spending blockchains are ignored until the legitimate blockchain has been confirmed
        return ((DSBlockchain) newChain).isDoubleSpending() 
            && ((DSBlockchain) blockchain).getLength() < p.getConfirmations();
    }

    @Override
    protected void onChoice(Blockchain oldChain, Blockchain newChain) {
        boolean doubleSpendingBefore = ((DSBlockchain) oldChain).isDoubleSpending();
        boolean doubleSpendingAfter = ((DSBlockchain) newChain).isDoubleSpending();
        if(doubleSpendingBefore && !doubleSpendingAfter){
            Logger.log(Level.FINEST, String.format("%s: No longer convinced of double-spending transaction.",name));
            dsm.removeConvinced();
        }else if(!doubleSpendingBefore && doubleSpendingAfter){
            Logger.log(Level.FINEST, String.format("%s: Is now convinced of double-spending transaction.",name));
            dsm.addConvinced();
        }
    }
}
